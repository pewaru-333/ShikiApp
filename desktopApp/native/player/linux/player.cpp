#include <gst/gst.h>
#include <gst/app/gstappsink.h>
#include <gst/video/video.h>
#include <algorithm>
#include <cmath>
#include <cstdint>
#include <cstdlib>
#include <cstring>
#include <limits>
#include <new>
#include <string>
#include <utility>
#include <vector>

namespace {

enum class Command : int {
    Play, Pause, SetVolume, SetSpeed, Seek, SelectAudio
};

struct Player {
    GstElement* pipeline = nullptr;
    GstElement* sink = nullptr;
    GstBus* bus = nullptr;
    GstSample* sample = nullptr;
    std::vector<std::pair<std::string, std::string>> headers;
    std::string uri;
    std::string subtitle;
    bool failed = false;
    bool ended = false;
    bool ready = false;
    bool wantsPlay = false;
    bool live = false;
    int buffering = 100;
    int audio = -1;
    double speed = 1;
    double volume = 1;
    double pendingPosition = -1;
    int outputWidth = 0;
    int outputHeight = 0;

    ~Player() { stop(); }

    void stop() {
        if (pipeline) gst_element_set_state(pipeline, GST_STATE_NULL);
        if (sample) gst_sample_unref(std::exchange(sample, nullptr));
        if (bus) gst_object_unref(std::exchange(bus, nullptr));
        if (pipeline) gst_object_unref(std::exchange(pipeline, nullptr));
        sink = nullptr;
        ready = false;
    }

    static void configureSource(GstElement*, GstElement* element, gpointer data) {
        auto& player = *static_cast<Player*>(data);
        if (!g_object_class_find_property(G_OBJECT_GET_CLASS(element), "extra-headers")) return;
        GstStructure* values = gst_structure_new_empty("headers");
        for (auto const& [name, value] : player.headers) {
            if (g_ascii_strcasecmp(name.c_str(), "Origin") != 0 &&
                g_ascii_strcasecmp(name.c_str(), "Referer") != 0 &&
                g_ascii_strcasecmp(name.c_str(), "User-Agent") != 0) continue;
            if (g_ascii_strcasecmp(name.c_str(), "User-Agent") == 0 &&
                g_object_class_find_property(G_OBJECT_GET_CLASS(element), "user-agent")) {
                g_object_set(element, "user-agent", value.c_str(), nullptr);
            } else {
                gst_structure_set(values, name.c_str(), G_TYPE_STRING, value.c_str(), nullptr);
            }
        }
        g_object_set(element, "extra-headers", values, nullptr);
        gst_structure_free(values);
    }

    static void configureElement(GstBin*, GstBin*, GstElement* element, gpointer data) {
        configureSource(nullptr, element, data);
    }

    bool open() {
        stop();
        failed = false;
        ended = false;
        buffering = 100;
        pipeline = gst_element_factory_make("playbin", nullptr);
        if (!pipeline) return false;
        gst_object_ref_sink(pipeline);
        GstElement* output = gst_parse_bin_from_description(
            "videoconvert ! videoscale ! appsink name=frames", TRUE, nullptr);
        if (!output) return false;
        gst_object_ref_sink(output);
        sink = gst_bin_get_by_name(GST_BIN(output), "frames");
        if (!sink) {
            gst_object_unref(output);
            return false;
        }
        GstCaps* caps = gst_caps_new_simple("video/x-raw", "format", G_TYPE_STRING, "BGRA",
            "pixel-aspect-ratio", GST_TYPE_FRACTION, 1, 1, nullptr);
        gst_app_sink_set_caps(GST_APP_SINK(sink), caps);
        gst_caps_unref(caps);
        g_object_set(sink, "max-buffers", 1u, "drop", TRUE, "sync", TRUE,
            "wait-on-eos", FALSE, "enable-last-sample", FALSE, nullptr);
        g_object_set(pipeline, "video-sink", output, "uri", uri.c_str(),
            "suburi", subtitle.empty() ? nullptr : subtitle.c_str(),
            "volume", volume, "current-audio", audio,
            "subtitle-font-desc", "Arial 28", nullptr);
        gst_object_unref(output);
        gst_object_unref(sink);
        if (outputWidth > 0 && outputHeight > 0) resize(outputWidth, outputHeight);
        GstElement* tempo = gst_element_factory_make("scaletempo", nullptr);
        if (tempo) {
            gst_object_ref_sink(tempo);
            g_object_set(pipeline, "audio-filter", tempo, nullptr);
            gst_object_unref(tempo);
        }
        g_signal_connect(pipeline, "source-setup", G_CALLBACK(configureSource), this);
        g_signal_connect(pipeline, "deep-element-added", G_CALLBACK(configureElement), this);
        guint flags = 0;
        g_object_get(pipeline, "flags", &flags, nullptr);
        constexpr guint textFlag = 1u << 2;
        flags = subtitle.empty() ? flags & ~textFlag : flags | textFlag;
        g_object_set(pipeline, "flags", flags, nullptr);
        bus = gst_element_get_bus(pipeline);
        const auto result = gst_element_set_state(pipeline, GST_STATE_PAUSED);
        live = result == GST_STATE_CHANGE_NO_PREROLL;
        return result != GST_STATE_CHANGE_FAILURE;
    }

    bool resize(int width, int height) {
        outputWidth = width;
        outputHeight = height;
        if (!sink) return true;
        GstCaps* caps = gst_caps_new_simple("video/x-raw", "format", G_TYPE_STRING, "BGRA",
            "width", G_TYPE_INT, width, "height", G_TYPE_INT, height,
            "pixel-aspect-ratio", GST_TYPE_FRACTION, 1, 1, nullptr);
        gst_app_sink_set_caps(GST_APP_SINK(sink), caps);
        gst_caps_unref(caps);
        if (ready) {
            GstPad* pad = gst_element_get_static_pad(sink, "sink");
            gst_pad_push_event(pad, gst_event_new_reconfigure());
            gst_object_unref(pad);
            if (!wantsPlay) seek(position());
        }
        return true;
    }

    double position() const {
        gint64 time = 0;
        return pipeline && gst_element_query_position(pipeline, GST_FORMAT_TIME, &time) && time >= 0
            ? static_cast<double>(time) / GST_SECOND : 0;
    }

    bool seek(double seconds) {
        const auto success = gst_element_seek(pipeline, speed, GST_FORMAT_TIME,
            static_cast<GstSeekFlags>(GST_SEEK_FLAG_FLUSH | GST_SEEK_FLAG_ACCURATE),
            GST_SEEK_TYPE_SET, static_cast<gint64>(seconds * GST_SECOND),
            GST_SEEK_TYPE_NONE, -1);
        if (success) {
            ended = false;
            if (sample) gst_sample_unref(std::exchange(sample, nullptr));
        }
        return success;
    }

    void poll() {
        while (GstMessage* message = gst_bus_pop(bus)) {
            switch (GST_MESSAGE_TYPE(message)) {
                case GST_MESSAGE_ERROR: failed = true; break;
                case GST_MESSAGE_EOS:
                    ended = true;
                    wantsPlay = false;
                    gst_element_set_state(pipeline, GST_STATE_PAUSED);
                    break;
                case GST_MESSAGE_BUFFERING:
                    gst_message_parse_buffering(message, &buffering);
                    if (!live && ready && wantsPlay) {
                        gst_element_set_state(pipeline, buffering < 100 ? GST_STATE_PAUSED : GST_STATE_PLAYING);
                    }
                    break;
                case GST_MESSAGE_CLOCK_LOST:
                    if (wantsPlay) {
                        gst_element_set_state(pipeline, GST_STATE_PAUSED);
                        gst_element_set_state(pipeline, GST_STATE_PLAYING);
                    }
                    break;
                default: break;
            }
            gst_message_unref(message);
        }
        GstState state = GST_STATE_NULL;
        GstState pending = GST_STATE_VOID_PENDING;
        if (gst_element_get_state(pipeline, &state, &pending, 0) == GST_STATE_CHANGE_FAILURE) failed = true;
        if (!ready && !failed && state >= GST_STATE_PAUSED && pending == GST_STATE_VOID_PENDING) {
            ready = true;
            if (pendingPosition >= 0 || speed != 1) {
                seek(std::max(0.0, pendingPosition));
                pendingPosition = -1;
            }
            if (wantsPlay && (live || buffering == 100)) gst_element_set_state(pipeline, GST_STATE_PLAYING);
        }
    }
};

}

#define API extern "C" __attribute__((visibility("default")))

API Player* lp_create() {
    if (!gst_init_check(nullptr, nullptr, nullptr)) return nullptr;
    return new (std::nothrow) Player();
}

API void lp_destroy(Player* player) { delete player; }

API int lp_load(Player* player, char const* uri, char const* const* names,
    char const* const* values, int count, int audio, char const* subtitle) {
    if (!player || !uri || !gst_uri_is_valid(uri) || count < 0 || audio < -1 ||
        (count > 0 && (!names || !values))) return -1;
    try {
        player->stop();
        player->headers.clear();
        for (int i = 0; i < count; ++i) {
            if (!names[i] || !values[i] || std::strpbrk(names[i], "\r\n") || std::strpbrk(values[i], "\r\n")) return -1;
            player->headers.emplace_back(names[i], values[i]);
        }
        player->uri = uri;
        player->subtitle = subtitle ? subtitle : "";
        player->audio = audio;
        player->speed = 1;
        player->pendingPosition = -1;
        player->wantsPlay = false;
        player->outputWidth = 0;
        player->outputHeight = 0;
        return player->open() ? 0 : -1;
    } catch (...) { return -1; }
}

API int lp_control(Player* player, int command, double value) {
    if (!player || !player->pipeline || !std::isfinite(value)) return -1;
    switch (static_cast<Command>(command)) {
        case Command::Play:
            if (player->ended && !player->seek(0)) return -1;
            player->wantsPlay = true;
            if (player->ready && (player->live || player->buffering == 100))
                return gst_element_set_state(player->pipeline, GST_STATE_PLAYING) == GST_STATE_CHANGE_FAILURE ? -1 : 0;
            return 0;
        case Command::Pause:
            player->wantsPlay = false;
            if (player->ready)
                return gst_element_set_state(player->pipeline, GST_STATE_PAUSED) == GST_STATE_CHANGE_FAILURE ? -1 : 0;
            return 0;
        case Command::SetVolume:
            player->volume = std::clamp(value, 0.0, 1.0);
            g_object_set(player->pipeline, "volume", player->volume, nullptr);
            return 0;
        case Command::SetSpeed:
            if (value <= 0) return -1;
            if (player->speed == value) return 0;
            player->speed = value;
            return !player->ready || player->seek(player->position()) ? 0 : -1;
        case Command::Seek:
            if (value < 0 || value >= static_cast<double>(std::numeric_limits<gint64>::max()) / GST_SECOND) return -1;
            if (!player->ready) { player->pendingPosition = value; return 0; }
            return player->seek(value) ? 0 : -1;
        case Command::SelectAudio:
            if (value < -1 || value > std::numeric_limits<int>::max()) return -1;
            player->audio = static_cast<int>(value);
            g_object_set(player->pipeline, "current-audio", player->audio, nullptr);
            return 0;
        default: return -1;
    }
}

API int lp_subtitle(Player* player, char const* uri) {
    if (!player || !player->pipeline || (uri && *uri && !gst_uri_is_valid(uri))) return -1;
    try {
        const std::string selected = uri ? uri : "";
        if (selected == player->subtitle) return 0;
        const double position = player->position();
        player->subtitle = selected;
        player->pendingPosition = position;
        return player->open() ? 0 : -1;
    } catch (...) { return -1; }
}

API int lp_resize(Player* player, int width, int height) {
    if (!player || width <= 0 || height <= 0 || width > 8192 || height > 8192) return -1;
    return player->resize(width, height) ? 0 : -1;
}

API int lp_status(Player* player, double* status) {
    if (!player || !player->pipeline || !status) return -1;
    player->poll();
    GstState state = GST_STATE_NULL;
    gst_element_get_state(player->pipeline, &state, nullptr, 0);
    gint64 duration = 0;
    gst_element_query_duration(player->pipeline, GST_FORMAT_TIME, &duration);
    int width = 0;
    int height = 0;
    GstPad* pad = nullptr;
    g_signal_emit_by_name(player->pipeline, "get-video-pad", 0, &pad);
    if (pad) {
        GstCaps* caps = gst_pad_get_current_caps(pad);
        GstVideoInfo info{};
        if (caps && gst_video_info_from_caps(&info, caps)) {
            width = GST_VIDEO_INFO_WIDTH(&info);
            height = GST_VIDEO_INFO_HEIGHT(&info);
            if (info.par_d > 0 && info.par_n > 0)
                width = static_cast<int>(std::round(static_cast<double>(width) * info.par_n / info.par_d));
        }
        if (caps) gst_caps_unref(caps);
        gst_object_unref(pad);
    }
    status[0] = player->ready;
    status[1] = player->failed ? -1 : 0;
    status[2] = player->ended;
    status[3] = state == GST_STATE_PLAYING;
    status[4] = player->position();
    status[5] = duration > 0 ? static_cast<double>(duration) / GST_SECOND : 0;
    status[6] = width;
    status[7] = height;
    status[8] = player->buffering / 100.0;
    return 0;
}

API int lp_frame(Player* player, uint8_t* destination, int capacity, int* dimensions) {
    if (!player || !player->sink || !dimensions || capacity < 0) return -1;
    if (!player->sample) {
        GstSample* preroll = gst_app_sink_try_pull_preroll(GST_APP_SINK(player->sink), 0);
        GstSample* sample = gst_app_sink_try_pull_sample(GST_APP_SINK(player->sink), 0);
        if (preroll) {
            GstState state = GST_STATE_NULL;
            gst_element_get_state(player->pipeline, &state, nullptr, 0);
            if (state == GST_STATE_PAUSED) {
                if (sample) gst_sample_unref(sample);
                sample = preroll;
            } else {
                gst_sample_unref(preroll);
            }
        }
        player->sample = sample;
    }
    if (!player->sample) return 0;
    GstVideoInfo info{};
    if (!gst_video_info_from_caps(&info, gst_sample_get_caps(player->sample)) ||
        GST_VIDEO_INFO_FORMAT(&info) != GST_VIDEO_FORMAT_BGRA) return -1;
    const int width = GST_VIDEO_INFO_WIDTH(&info);
    const int height = GST_VIDEO_INFO_HEIGHT(&info);
    if (width <= 0 || height <= 0 || width > 8192 || height > 8192) return -1;
    dimensions[0] = width;
    dimensions[1] = height;
    const int size = width * height * 4;
    if (!destination || capacity < size) return size;
    GstVideoFrame frame{};
    if (!gst_video_frame_map(&frame, &info, gst_sample_get_buffer(player->sample), GST_MAP_READ)) return -1;
    const int stride = GST_VIDEO_FRAME_PLANE_STRIDE(&frame, 0);
    if (std::abs(static_cast<int64_t>(stride)) < static_cast<int64_t>(width) * 4) {
        gst_video_frame_unmap(&frame);
        return -1;
    }
    auto source = static_cast<uint8_t const*>(GST_VIDEO_FRAME_PLANE_DATA(&frame, 0));
    for (int row = 0; row < height; ++row) {
        std::memcpy(destination, source, static_cast<size_t>(width) * 4);
        source += stride;
        destination += width * 4;
    }
    gst_video_frame_unmap(&frame);
    gst_sample_unref(std::exchange(player->sample, nullptr));
    return size;
}
