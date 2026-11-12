#include <windows.h>
#include <d3d11_4.h>
#include <dxgi.h>
#include <windows.graphics.directx.direct3d11.interop.h>
#include <winrt/base.h>
#include <winrt/Windows.Foundation.h>
#include <winrt/Windows.Foundation.Collections.h>
#include <winrt/Windows.Data.Json.h>
#include <winrt/Windows.Media.Core.h>
#include <winrt/Windows.Media.Playback.h>
#include <winrt/Windows.Media.Streaming.Adaptive.h>
#include <winrt/Windows.Web.Http.h>
#include <winrt/Windows.Web.Http.Headers.h>
#include <winrt/Windows.Graphics.DirectX.Direct3D11.h>
#include <winrt/Windows.Storage.Streams.h>
#include <winrt/Windows.UI.h>
#include <algorithm>
#include <atomic>
#include <chrono>
#include <cmath>
#include <cstdint>
#include <limits>
#include <utility>
#include <memory>
#include <mutex>
#include <cstring>
#include <vector>
#include <ass.h>
#include <filesystem>
#include <stdexcept>

using namespace winrt;
using namespace winrt::Windows::Foundation;
using namespace winrt::Windows::Media::Core;
using namespace winrt::Windows::Media::Playback;
using namespace winrt::Windows::Media::Streaming::Adaptive;
using namespace winrt::Windows::Graphics::DirectX::Direct3D11;
using namespace winrt::Windows::Storage::Streams;

class AssRenderer {
    struct Api {
        HMODULE module = nullptr;
        decltype(&ass_library_init) libraryInit = nullptr;
        decltype(&ass_library_done) libraryDone = nullptr;
        decltype(&ass_set_message_cb) setMessageCallback = nullptr;
        decltype(&ass_set_extract_fonts) setExtractFonts = nullptr;
        decltype(&ass_renderer_init) rendererInit = nullptr;
        decltype(&ass_renderer_done) rendererDone = nullptr;
        decltype(&ass_read_memory) readMemory = nullptr;
        decltype(&ass_free_track) freeTrack = nullptr;
        decltype(&ass_set_fonts) setFonts = nullptr;
        decltype(&ass_set_frame_size) setFrameSize = nullptr;
        decltype(&ass_set_storage_size) setStorageSize = nullptr;
        decltype(&ass_render_frame) renderFrame = nullptr;

        template<typename T>
        T function(char const* name) {
            auto address = GetProcAddress(module, name);
            if (!address) {
                throw std::runtime_error("Missing libass function");
            }
            return reinterpret_cast<T>(address);
        }

        static void quiet(int, char const*, va_list, void*) {}

        Api() {
            HMODULE owner = nullptr;
            if (!GetModuleHandleExW(GET_MODULE_HANDLE_EX_FLAG_FROM_ADDRESS |
                GET_MODULE_HANDLE_EX_FLAG_UNCHANGED_REFCOUNT, reinterpret_cast<LPCWSTR>(&quiet), &owner)) {
                throw std::runtime_error("Player module unavailable");
            }
            std::vector<wchar_t> path(32768);
            const auto length = GetModuleFileNameW(owner, path.data(), static_cast<DWORD>(path.size()));
            if (length == 0 || length >= path.size()) {
                throw std::runtime_error("Player module path unavailable");
            }
            const auto library = std::filesystem::path(path.data()).parent_path() / L"libass" / L"libass-9.dll";
            module = LoadLibraryExW(library.c_str(), nullptr,
                LOAD_LIBRARY_SEARCH_DLL_LOAD_DIR | LOAD_LIBRARY_SEARCH_DEFAULT_DIRS);
            if (!module) {
                throw std::runtime_error("libass unavailable");
            }
            try {
                libraryInit = function<decltype(libraryInit)>("ass_library_init");
                libraryDone = function<decltype(libraryDone)>("ass_library_done");
                setMessageCallback = function<decltype(setMessageCallback)>("ass_set_message_cb");
                setExtractFonts = function<decltype(setExtractFonts)>("ass_set_extract_fonts");
                rendererInit = function<decltype(rendererInit)>("ass_renderer_init");
                rendererDone = function<decltype(rendererDone)>("ass_renderer_done");
                readMemory = function<decltype(readMemory)>("ass_read_memory");
                freeTrack = function<decltype(freeTrack)>("ass_free_track");
                setFonts = function<decltype(setFonts)>("ass_set_fonts");
                setFrameSize = function<decltype(setFrameSize)>("ass_set_frame_size");
                setStorageSize = function<decltype(setStorageSize)>("ass_set_storage_size");
                renderFrame = function<decltype(renderFrame)>("ass_render_frame");
            } catch (...) {
                FreeLibrary(module);
                throw;
            }
        }

        ~Api() {
            FreeLibrary(module);
        }

        Api(Api const&) = delete;
        Api& operator=(Api const&) = delete;
    };

    std::shared_ptr<Api> api;
    ASS_Library* library = nullptr;
    ASS_Renderer* renderer = nullptr;
    ASS_Track* track = nullptr;

    void reset() noexcept {
        if (track) {
            api->freeTrack(track);
            track = nullptr;
        }
        if (renderer) {
            api->rendererDone(renderer);
            renderer = nullptr;
        }
        if (library) {
            api->libraryDone(library);
            library = nullptr;
        }
    }

public:
    AssRenderer(uint8_t const* bytes, int size) : api(std::make_shared<Api>()) {
        try {
            library = api->libraryInit();
            if (!library) {
                throw std::runtime_error("libass initialization failed");
            }
            api->setMessageCallback(library, &Api::quiet, nullptr);
            api->setExtractFonts(library, 1);
            renderer = api->rendererInit(library);
            if (!renderer) {
                throw std::runtime_error("libass renderer initialization failed");
            }
            std::vector<char> content(bytes, bytes + size);
            content.push_back('\0');
            track = api->readMemory(library, content.data(), static_cast<size_t>(size), "UTF-8");
            if (!track) {
                throw std::runtime_error("Invalid ASS subtitle");
            }
            api->setFonts(renderer, nullptr, "Arial", ASS_FONTPROVIDER_DIRECTWRITE, nullptr, 1);
        } catch (...) {
            reset();
            throw;
        }
    }

    ~AssRenderer() {
        reset();
    }

    AssRenderer(AssRenderer const&) = delete;
    AssRenderer& operator=(AssRenderer const&) = delete;

    void render(uint8_t* pixels, int width, int height, int sourceWidth, int sourceHeight, int64_t time) {
        api->setFrameSize(renderer, width, height);
        api->setStorageSize(renderer, sourceWidth, sourceHeight);
        int changed = 0;
        for (auto image = api->renderFrame(renderer, track, time, &changed); image; image = image->next) {
            if (!image->bitmap || image->w <= 0 || image->h <= 0 || image->stride < image->w) {
                continue;
            }
            const auto opacity = 255 - (image->color & 255);
            const uint32_t color[] = {
                (image->color >> 8) & 255, (image->color >> 16) & 255, (image->color >> 24) & 255
            };
            const int left = std::max(0, -image->dst_x);
            const int top = std::max(0, -image->dst_y);
            const int right = std::min(image->w, width - image->dst_x);
            const int bottom = std::min(image->h, height - image->dst_y);
            if (right <= left || bottom <= top) {
                continue;
            }
            for (int y = top; y < bottom; ++y) {
                auto mask = image->bitmap + static_cast<size_t>(y) * image->stride;
                auto row = pixels + (static_cast<size_t>(y + image->dst_y) * width + image->dst_x + left) * 4;
                for (int x = left; x < right; ++x, row += 4) {
                    const auto alpha = (mask[x] * opacity + 127) / 255;
                    for (int channel = 0; channel < 3; ++channel) {
                        row[channel] = static_cast<uint8_t>((color[channel] * alpha +
                            row[channel] * (255 - alpha) + 127) / 255);
                    }
                }
            }
        }
    }
};

struct Player : std::enable_shared_from_this<Player> {
    MediaPlayer media{nullptr};
    MediaPlaybackItem item{nullptr};
    MediaSource source{nullptr};
    std::unique_ptr<AssRenderer> ass;
    TimedTextSource subtitleSource{nullptr}, pendingSubtitle{nullptr};
    std::mutex subtitleMutex;
    std::vector<TimedMetadataTrack> resolvedSubtitles;
    int subtitleRequest = 0, subtitleResult = 0;
    bool subtitlesEnabled = false;
    std::atomic<bool> subtitleDirty{false};
    com_ptr<ID3D11Device> device;
    com_ptr<ID3D11DeviceContext> context;
    com_ptr<ID3D11Texture2D> texture, staging;
    IDirect3DSurface surface{nullptr};
    std::mutex frames;
    int width = 0, height = 0;
    int viewportWidth = 1920, viewportHeight = 1080;
    bool framePending = false;
    bool hasFrame = false;
    std::atomic<int> opened{0}, failed{0}, ended{0};
    std::atomic<uint64_t> generation{0};
    int audio = -1;

    void stop() {
        {
            std::lock_guard guard(frames);
            ++generation;
            opened = 0;
            failed = 0;
            ended = 0;
            surface = nullptr;
            texture = nullptr;
            staging = nullptr;
            width = height = 0;
            framePending = false;
            hasFrame = false;
            subtitlesEnabled = false;
            ass.reset();
            subtitleDirty = false;
        }
        {
            std::lock_guard guard(subtitleMutex);
            subtitleRequest = 0;
            subtitleResult = 0;
            resolvedSubtitles.clear();
        }
        pendingSubtitle = nullptr;
        subtitleSource = nullptr;
        auto previous = std::exchange(media, nullptr);
        item = nullptr;
        if (previous) previous.Close();
        source = nullptr;
    }

    void frame(MediaPlayer const& sender, uint64_t expected) noexcept {
        try {
            std::lock_guard guard(frames);
            if (generation != expected || framePending) return;
            auto session = sender.PlaybackSession();
            int w = static_cast<int>(session.NaturalVideoWidth());
            int h = static_cast<int>(session.NaturalVideoHeight());
            if (w <= 0 || h <= 0 || w > 8192 || h > 8192) return;
            const double scale = std::min(1.0, std::min(static_cast<double>(viewportWidth) / w, static_cast<double>(viewportHeight) / h));
            w = std::max(1, static_cast<int>(std::round(w * scale)));
            h = std::max(1, static_cast<int>(std::round(h * scale)));
            if (!texture || w != width || h != height) {
                width = w;
                height = h;
                surface = nullptr;
                texture = nullptr;
                staging = nullptr;
                D3D11_TEXTURE2D_DESC desc{};
                desc.Width = w;
                desc.Height = h;
                desc.MipLevels = 1;
                desc.ArraySize = 1;
                desc.Format = DXGI_FORMAT_B8G8R8A8_UNORM;
                desc.SampleDesc.Count = 1;
                desc.BindFlags = D3D11_BIND_RENDER_TARGET | D3D11_BIND_SHADER_RESOURCE;
                check_hresult(device->CreateTexture2D(&desc, nullptr, texture.put()));
                desc.BindFlags = 0;
                desc.Usage = D3D11_USAGE_STAGING;
                desc.CPUAccessFlags = D3D11_CPU_ACCESS_READ;
                check_hresult(device->CreateTexture2D(&desc, nullptr, staging.put()));
                com_ptr<::IInspectable> inspectable;
                check_hresult(CreateDirect3D11SurfaceFromDXGISurface(texture.as<IDXGISurface>().get(), inspectable.put()));
                surface = inspectable.as<IDirect3DSurface>();
            }
            sender.CopyFrameToVideoSurface(surface);
            if (subtitlesEnabled) {
                sender.RenderSubtitlesToSurface(surface, Rect{0, 0, static_cast<float>(width), static_cast<float>(height)});
            }
            context->CopyResource(staging.get(), texture.get());
            context->Flush();
            framePending = true;
            hasFrame = true;
        } catch (...) {
            auto error = to_hresult();
            std::lock_guard guard(frames);
            if (generation == expected) failed = error;
        }
    }

    void resize(int w, int h) {
        bool redraw;
        {
            std::lock_guard guard(frames);
            w = std::min(w, 8192);
            h = std::min(h, 8192);
            if (w == viewportWidth && h == viewportHeight) return;
            viewportWidth = w;
            viewportHeight = h;
            framePending = false;
            redraw = hasFrame && opened && !failed;
        }
        if (redraw) frame(media, generation.load());
    }

    void seek(double seconds) {
        auto session = media.PlaybackSession();
        if (!session.CanSeek()) return;
        {
            std::lock_guard guard(frames);
            framePending = false;
        }
        session.Position(TimeSpan{static_cast<int64_t>(seconds * 10000000.0)});
        ended = 0;
    }

    void removeSubtitle(TimedTextSource& value) {
        if (!value) return;
        uint32_t index;
        if (source && source.ExternalTimedTextSources().IndexOf(value, index)) {
            source.ExternalTimedTextSources().RemoveAt(index);
        }
        value = nullptr;
    }

    void setSubtitleModes(std::vector<uint32_t> const& enabled) {
        auto tracks = item.TimedMetadataTracks();
        std::lock_guard guard(frames);
        for (uint32_t i = 0; i < tracks.Size(); ++i) {
            auto kind = tracks.GetAt(i).TimedMetadataKind();
            if (kind == TimedMetadataKind::Subtitle || kind == TimedMetadataKind::Caption ||
                kind == TimedMetadataKind::ImageSubtitle) {
                tracks.SetPresentationMode(i, std::find(enabled.begin(), enabled.end(), i) != enabled.end()
                    ? TimedMetadataTrackPresentationMode::PlatformPresented
                    : TimedMetadataTrackPresentationMode::Disabled);
            }
        }
        if (!enabled.empty()) {
            ass.reset();
        }
        subtitlesEnabled = !enabled.empty();
        framePending = false;
        subtitleDirty = true;
    }

    static TimedTextStyle createDefaultSubtitleStyle(TimedTextStyle const& original = nullptr) {
        auto style = TimedTextStyle();
        style.FontFamily(L"Arial");

        TimedTextDouble fontSize{};
        fontSize.Unit = TimedTextUnit::Percentage;
        fontSize.Value = 100;
        style.FontSize(fontSize);

        style.LineAlignment(TimedTextLineAlignment::Center);
        style.FontStyle(original ? original.FontStyle() : TimedTextFontStyle::Normal);
        style.FontWeight(original ? original.FontWeight() : TimedTextWeight::Normal);
        style.Foreground(winrt::Windows::UI::Colors::White());
        style.Background(winrt::Windows::UI::Color{153, 0, 0, 0});
        style.IsBackgroundAlwaysShown(false);

        TimedTextDouble outlineThickness{};
        outlineThickness.Unit = TimedTextUnit::Percentage;
        outlineThickness.Value = 4.5;
        style.OutlineThickness(outlineThickness);

        style.FlowDirection(original ? original.FlowDirection() : TimedTextFlowDirection::LeftToRight);
        style.OutlineColor(winrt::Windows::UI::Color{0x80, 0, 0, 0});
        if (original) {
            style.IsUnderlineEnabled(original.IsUnderlineEnabled());
            style.IsLineThroughEnabled(original.IsLineThroughEnabled());
            style.IsOverlineEnabled(original.IsOverlineEnabled());
        }
        return style;
    }

    static TimedTextRegion createDefaultSubtitleRegion() {
        auto region = TimedTextRegion();
        TimedTextSize extent{};
        extent.Unit = TimedTextUnit::Percentage;
        extent.Width = 100;
        extent.Height = 88;
        region.Extent(extent);
        TimedTextPoint position{};
        position.Unit = TimedTextUnit::Pixels;
        position.X = 0;
        position.Y = 0;
        region.Position(position);
        region.DisplayAlignment(TimedTextDisplayAlignment::After);
        region.Background(winrt::Windows::UI::Colors::Transparent());
        region.TextWrapping(TimedTextWrapping::Wrap);
        region.IsOverflowClipped(false);
        region.LineHeight(TimedTextDouble{100, TimedTextUnit::Percentage});
        return region;
    }

    static void styleSubtitles(TimedMetadataTrack const& track) {
        const auto region = createDefaultSubtitleRegion();
        for (auto const& value : track.Cues()) {
            auto cue = value.try_as<TimedTextCue>();
            if (!cue) {
                continue;
            }
            cue.CueStyle(createDefaultSubtitleStyle(cue.CueStyle()));
            cue.CueRegion(region);
            for (auto const& line : cue.Lines()) {
                for (auto const& format : line.Subformats()) {
                    format.SubformatStyle(createDefaultSubtitleStyle(format.SubformatStyle()));
                }
            }
        }
    }

    void subtitle(uint8_t const* bytes, int size, int request) {
        if (!source || !item) throw hresult_error(E_UNEXPECTED);
        {
            std::lock_guard guard(subtitleMutex);
            subtitleRequest = request;
            subtitleResult = 0;
            resolvedSubtitles.clear();
        }
        removeSubtitle(pendingSubtitle);
        if (size < 0) return;
        if (size == 0) {
            setSubtitleModes({});
            removeSubtitle(subtitleSource);
            {
                std::lock_guard guard(frames);
                ass.reset();
            }
            std::lock_guard guard(subtitleMutex);
            subtitleResult = 1;
            return;
        }

        InMemoryRandomAccessStream stream;
        DataWriter writer(stream);
        writer.WriteBytes(array_view<uint8_t const>(bytes, bytes + size));
        writer.StoreAsync().get();
        writer.DetachStream();
        writer.Close();
        stream.Seek(0);
        auto candidate = TimedTextSource::CreateFromStream(stream);
        auto weak = weak_from_this();
        auto expected = generation.load();
        candidate.Resolved([weak, expected, request](auto const&, auto const& args) {
            if (auto self = weak.lock()) {
                try {
                    {
                        std::lock_guard guard(self->subtitleMutex);
                        if (self->generation != expected || self->subtitleRequest != request) return;
                    }

                    const auto error = args.Error();
                    std::vector<TimedMetadataTrack> resolved;
                    if (!error) {
                        for (auto const& track : args.Tracks()) {
                            auto kind = track.TimedMetadataKind();
                            if (kind == TimedMetadataKind::Subtitle || kind == TimedMetadataKind::Caption ||
                                kind == TimedMetadataKind::ImageSubtitle) {
                                styleSubtitles(track);
                                resolved.push_back(track);
                            }
                        }
                    }

                    std::lock_guard guard(self->subtitleMutex);
                    if (self->generation != expected || self->subtitleRequest != request) return;
                    self->subtitleResult = resolved.empty() ? E_FAIL : 0;
                    self->resolvedSubtitles = std::move(resolved);
                } catch (...) {
                    const auto error = to_hresult();
                    std::lock_guard guard(self->subtitleMutex);
                    if (self->generation == expected && self->subtitleRequest == request) {
                        self->subtitleResult = error;
                        self->resolvedSubtitles.clear();
                    }
                }
            }
        });
        pendingSubtitle = candidate;
        source.ExternalTimedTextSources().Append(candidate);
    }

    void subtitleAss(uint8_t const* bytes, int size, int request) {
        if (!source || !item) {
            throw hresult_error(E_UNEXPECTED);
        }
        auto candidate = std::make_unique<AssRenderer>(bytes, size);
        subtitle(nullptr, -1, request);
        setSubtitleModes({});
        removeSubtitle(subtitleSource);
        {
            std::lock_guard guard(frames);
            ass = std::move(candidate);
            framePending = false;
            subtitleDirty = true;
        }
        std::lock_guard guard(subtitleMutex);
        subtitleResult = 1;
    }

    int subtitleStatus(int request) {
        std::vector<TimedMetadataTrack> resolved;
        int result;
        {
            std::lock_guard guard(subtitleMutex);
            if (request != subtitleRequest) return E_ABORT;
            result = subtitleResult;
            if (result == 1 || FAILED(result)) return result;
            if (resolvedSubtitles.empty()) return 0;
            resolved = resolvedSubtitles;
        }
        auto tracks = item.TimedMetadataTracks();
        std::vector<uint32_t> indices;
        for (auto const& track : resolved) {
            uint32_t index;
            if (!tracks.IndexOf(track, index)) return 0;
            indices.push_back(index);
        }
        setSubtitleModes(indices);
        removeSubtitle(subtitleSource);
        subtitleSource = std::exchange(pendingSubtitle, nullptr);
        {
            std::lock_guard guard(subtitleMutex);
            subtitleResult = 1;
            resolvedSubtitles.clear();
        }
        return 1;
    }

    void refreshSubtitles() {
        if (!subtitleDirty.exchange(false)) return;
        bool redraw;
        {
            std::lock_guard guard(frames);
            framePending = false;
            redraw = hasFrame && opened && !failed;
        }
        if (redraw) frame(media, generation.load());
    }

    void load(char const* url, char const* headers, int audioIndex) {
        stop();
        audio = audioIndex;
        const auto expected = generation.load();
        const auto requestHeaders = winrt::Windows::Data::Json::JsonObject::Parse(to_hstring(headers));
        const Uri uri(to_hstring(url));
        if (requestHeaders.Size() == 0) {
            source = MediaSource::CreateFromUri(uri);
        } else {
            winrt::Windows::Web::Http::HttpClient client;
            const auto defaultHeaders = client.DefaultRequestHeaders();
            for (auto const& entry : requestHeaders) {
                if (!defaultHeaders.TryAppendWithoutValidation(entry.Key(), entry.Value().GetString())) {
                    throw hresult_invalid_argument();
                }
            }
            auto operation = AdaptiveMediaSource::CreateFromUriAsync(uri, client);
            if (operation.wait_for(std::chrono::seconds(15)) == AsyncStatus::Started) {
                operation.Cancel();
                throw hresult_error(HRESULT_FROM_WIN32(ERROR_TIMEOUT));
            }
            const auto adaptive = operation.get();
            if (adaptive.Status() != AdaptiveMediaSourceCreationStatus::Success) {
                check_hresult(adaptive.ExtendedError());
                throw hresult_error(E_FAIL);
            }
            source = MediaSource::CreateFromAdaptiveMediaSource(adaptive.MediaSource());
        }
        item = MediaPlaybackItem(source);
        media = MediaPlayer();
        media.AutoPlay(false);
        media.IsVideoFrameServerEnabled(true);
        media.CommandManager().IsEnabled(false);
        const auto weak = weak_from_this();
        const auto invalidateSubtitles = [weak, expected](auto const&, auto const&) {
            if (auto self = weak.lock(); self && self->generation == expected) {
                self->subtitleDirty = true;
            }
        };
        media.VideoFrameAvailable([weak, expected](auto const& sender, auto const&) {
            if (auto self = weak.lock()) {
                self->frame(sender, expected);
            }
        });
        media.SubtitleFrameChanged(invalidateSubtitles);
        media.PlaybackSession().SeekCompleted(invalidateSubtitles);
        media.MediaOpened([weak, expected](auto const&, auto const&) {
            if (auto self = weak.lock()) {
                std::lock_guard guard(self->frames);
                if (self->generation == expected) self->opened = 1;
            }
        });
        media.MediaFailed([weak, expected](auto const&, auto const& args) {
            if (auto self = weak.lock()) {
                const auto error = args.ExtendedErrorCode();
                std::lock_guard guard(self->frames);
                if (self->generation == expected) {
                    self->failed = error.value < 0 ? error.value : E_FAIL;
                }
            }
        });
        media.MediaEnded([weak, expected](auto const&, auto const&) {
            if (auto self = weak.lock()) {
                std::lock_guard guard(self->frames);
                if (self->generation == expected) self->ended = 1;
            }
        });
        media.Source(item);
    }
};

using Handle = std::shared_ptr<Player>*;

enum class PlayerCommand : int {
    Play = 0,
    Pause = 1,
    SetVolume = 2,
    SetSpeed = 3,
    Seek = 4,
    SelectAudio = 5
};

constexpr int maxSubtitleBytes = 16 * 1024 * 1024;

#define API extern "C" __declspec(dllexport)

API void* wm_create() noexcept {
    bool initialized = false;
    try {
        init_apartment(apartment_type::multi_threaded);
        initialized = true;
        auto p = std::make_shared<Player>();
        auto result = D3D11CreateDevice(nullptr, D3D_DRIVER_TYPE_HARDWARE, nullptr,
            D3D11_CREATE_DEVICE_BGRA_SUPPORT | D3D11_CREATE_DEVICE_VIDEO_SUPPORT,
            nullptr, 0, D3D11_SDK_VERSION, p->device.put(), nullptr, p->context.put());
        if (FAILED(result)) {
            p->device = nullptr;
            p->context = nullptr;
            check_hresult(D3D11CreateDevice(nullptr, D3D_DRIVER_TYPE_WARP, nullptr,
                D3D11_CREATE_DEVICE_BGRA_SUPPORT, nullptr, 0, D3D11_SDK_VERSION,
                p->device.put(), nullptr, p->context.put()));
        }
        p->context.as<ID3D11Multithread>()->SetMultithreadProtected(TRUE);
        return new std::shared_ptr<Player>(std::move(p));
    } catch (...) {
        if (initialized) uninit_apartment();
        return nullptr;
    }
}

API void wm_destroy(Handle handle) noexcept {
    if (!handle) return;
    try {
        if (*handle) {
            (*handle)->stop();
        }
    } catch (...) {}
    delete handle;
    uninit_apartment();
}

API int wm_load(Handle h, char const* url, char const* headers, int audio) noexcept {
    if (!h || !*h || !url || !headers || audio < -1) return E_INVALIDARG;
    try {
        (*h)->load(url, headers, audio);
        return 0;
    } catch (...) {
        const auto error = to_hresult();
        try { (*h)->stop(); } catch (...) {}
        return error;
    }
}

API int wm_control(Handle h, int command, double value) noexcept {
    if (!h || !*h || !std::isfinite(value)) return E_INVALIDARG;
    try {
        auto& p = **h;
        if (!p.media) return E_UNEXPECTED;
        switch (static_cast<PlayerCommand>(command)) {
            case PlayerCommand::Play:
                if (p.ended.load()) {
                    p.seek(0);
                }
                p.media.Play();
                break;
            case PlayerCommand::Pause:
                p.media.Pause();
                break;
            case PlayerCommand::SetVolume:
                p.media.Volume(std::clamp(value, 0.0, 1.0));
                break;
            case PlayerCommand::SetSpeed:
                if (value <= 0) {
                    return E_INVALIDARG;
                }
                p.media.PlaybackSession().PlaybackRate(value);
                break;
            case PlayerCommand::Seek:
                if (value < 0 || value >= static_cast<double>(std::numeric_limits<int64_t>::max()) / 10000000.0) {
                    return E_INVALIDARG;
                }
                p.seek(value);
                break;
            case PlayerCommand::SelectAudio:
                if (value < -1 || value > std::numeric_limits<int>::max()) {
                    return E_INVALIDARG;
                }
                p.audio = static_cast<int>(value);
                break;
            default:
                return E_INVALIDARG;
        }
        return 0;
    } catch (...) { return to_hresult(); }
}

API int wm_status(Handle h, double* status) noexcept {
    if (!h || !*h || !status) return E_INVALIDARG;
    try {
        auto& p = **h;
        if (!p.media) return E_UNEXPECTED;
        const auto session = p.media.PlaybackSession();
        const auto opened = p.opened.load();
        if (opened && p.audio >= 0) {
            const auto tracks = p.item.AudioTracks();
            if (static_cast<uint32_t>(p.audio) < tracks.Size() && tracks.SelectedIndex() != p.audio) {
                tracks.SelectedIndex(p.audio);
            }
        }
        status[0] = opened;
        status[1] = p.failed;
        status[2] = p.ended;
        status[3] = static_cast<int>(session.PlaybackState());
        status[4] = session.Position().count() / 10000000.0;
        status[5] = session.NaturalDuration().count() / 10000000.0;
        status[6] = session.NaturalVideoWidth();
        status[7] = session.NaturalVideoHeight();
        return 0;
    } catch (...) { return to_hresult(); }
}

API int wm_resize(Handle h, int width, int height) noexcept {
    if (!h || !*h || width <= 0 || height <= 0) return E_INVALIDARG;
    try {
        (*h)->resize(width, height);
        return 0;
    } catch (...) { return to_hresult(); }
}

API int wm_subtitle(Handle h, uint8_t const* bytes, int size, int request) noexcept {
    if (!h || !*h || size < -1 || size > maxSubtitleBytes || (size > 0 && !bytes)) return E_INVALIDARG;
    try {
        (*h)->subtitle(bytes, size, request);
        return 0;
    } catch (...) { return to_hresult(); }
}

API int wm_subtitle_ass(Handle h, uint8_t const* bytes, int size, int request) noexcept {
    if (!h || !*h || !bytes || size <= 0 || size > maxSubtitleBytes) {
        return E_INVALIDARG;
    }
    try {
        (*h)->subtitleAss(bytes, size, request);
        return 0;
    } catch (...) {
        return to_hresult();
    }
}

API int wm_subtitle_status(Handle h, int request) noexcept {
    if (!h || !*h) return E_INVALIDARG;
    try { return (*h)->subtitleStatus(request); }
    catch (...) { return to_hresult(); }
}

API int wm_frame(Handle h, uint8_t* destination, int capacity, int* dimensions) noexcept {
    if (!h || !*h || !dimensions || capacity < 0) return E_INVALIDARG;
    try {
        auto& p = **h;
        p.refreshSubtitles();
        std::lock_guard guard(p.frames);
        dimensions[0] = p.width;
        dimensions[1] = p.height;
        if (!p.framePending) return 0;
        const int size = p.width * p.height * 4;
        if (!destination || capacity < size) return size;

        D3D11_MAPPED_SUBRESOURCE mapped{};
        const auto result = p.context->Map(p.staging.get(), 0, D3D11_MAP_READ, D3D11_MAP_FLAG_DO_NOT_WAIT, &mapped);
        if (result == DXGI_ERROR_WAS_STILL_DRAWING) return 0;
        check_hresult(result);
        const UINT rowBytes = static_cast<UINT>(p.width) * 4;
        if (mapped.RowPitch == rowBytes) {
            std::memcpy(destination, mapped.pData, size);
        } else {
            auto source = static_cast<uint8_t const*>(mapped.pData);
            auto target = destination;
            for (int row = 0; row < p.height; ++row) {
                std::memcpy(target, source, rowBytes);
                source += mapped.RowPitch;
                target += rowBytes;
            }
        }
        p.context->Unmap(p.staging.get(), 0);
        if (p.ass) {
            auto session = p.media.PlaybackSession();
            p.ass->render(destination, p.width, p.height, static_cast<int>(session.NaturalVideoWidth()),
                static_cast<int>(session.NaturalVideoHeight()), session.Position().count() / 10000);
        }
        p.framePending = false;
        return size;
    } catch (...) { return to_hresult(); }
}