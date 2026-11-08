use jni::objects::{JObject, JValue};
use jni::JavaVM;
use log::{error, info};

const MINECRAFT_PACKAGE: &str = "com.mojang.minecraftpe";

pub fn try_launch(app: &android_activity::AndroidApp) {
    match app.vm_as_ptr() {
        ptr if ptr.is_null() => error!("JavaVM недоступна"),
        ptr => {
            let vm = unsafe { JavaVM::from_raw(ptr as *mut _) };
            if let Err(e) = launch(&vm) {
                error!("Ошибка запуска: {e}");
            }
        }
    }
}

fn launch(vm: &JavaVM) -> Result<(), String> {
    let mut env = vm.attach_current_thread().map_err(|e| e.to_string())?;

    let activity = env
        .call_static_method(
            "org/kivy/android/PythonActivity",
            "mActivity",
            "()Landroid/app/Activity;",
            &[],
        )
        .or_else(|_| {
            env.call_static_method(
                "org/libsdl/app/SDLActivity",
                "getContext",
                "()Landroid/content/Context;",
                &[],
            )
        })
        .map_err(|e| e.to_string())?
        .l()
        .map_err(|e| e.to_string())?;

    let action = env.new_string("android.intent.action.MAIN").unwrap();
    let intent = env
        .new_object(
            "android/content/Intent",
            "(Ljava/lang/String;)V",
            &[JValue::Object(&JObject::from(action))],
        )
        .map_err(|e| e.to_string())?;

    let pkg = env.new_string(MINECRAFT_PACKAGE).unwrap();
    env.call_method(
        &intent,
        "setPackage",
        "(Ljava/lang/String;)Landroid/content/Intent;",
        &[JValue::Object(&JObject::from(pkg))],
    )
    .map_err(|e| e.to_string())?;

    env.call_method(
        &activity,
        "startActivity",
        "(Landroid/content/Intent;)V",
        &[JValue::Object(&intent)],
    )
    .map_err(|e| e.to_string())?;

    info!("Minecraft запущен");
    Ok(())
}
