package name.avioli.unilinks;

import android.content.BroadcastReceiver;
import android.content.Context;
import androidx.annotation.NonNull;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodChannel;

/** UniLinksPlugin */
public class UniLinksPlugin implements FlutterPlugin, ActivityAware {
  private static final String MESSAGES_CHANNEL = "uni_links/messages";
  private static final String EVENTS_CHANNEL = "uni_links/events";

  private Context context;
  private MethodChannel methodChannel;
  private EventChannel eventChannel;
  private BroadcastReceiver changeReceiver;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {
    setupChannels(binding.getBinaryMessenger(), binding.getApplicationContext());
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPlugin.FlutterPluginBinding binding) {
    teardownChannels();
  }

  private void setupChannels(BinaryMessenger messenger, Context context) {
    this.context = context;
    methodChannel = new MethodChannel(messenger, MESSAGES_CHANNEL);
    eventChannel = new EventChannel(messenger, EVENTS_CHANNEL);

    UniLinksMethodHandler methodHandler = new UniLinksMethodHandler(context);
    UniLinksStreamHandler streamHandler = new UniLinksStreamHandler(context);
    
    methodChannel.setMethodCallHandler(methodHandler);
    eventChannel.setStreamHandler(streamHandler);
    
    changeReceiver = streamHandler.getChangeReceiver();
    context.registerReceiver(changeReceiver, UniLinksStreamHandler.getIntentFilter());
  }

  private void teardownChannels() {
    methodChannel.setMethodCallHandler(null);
    eventChannel.setStreamHandler(null);
    if (changeReceiver != null) {
      context.unregisterReceiver(changeReceiver);
      changeReceiver = null;
    }
    methodChannel = null;
    eventChannel = null;
    context = null;
  }

  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    // Handle initial link if needed
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    // No-op
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    // No-op
  }

  @Override
  public void onDetachedFromActivity() {
    // No-op
  }
}
