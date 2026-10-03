import org.application.shikiapp.backend.dark.ProductServices as DarkServices
import org.application.shikiapp.shared.network.client.BridgeYggdrasilTransport

internal object ProductServices {
    fun create() = DarkServices.create(BridgeYggdrasilTransport)
}
