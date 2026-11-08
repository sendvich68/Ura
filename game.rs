use jni::objects::{JObject, JValue};
use jni::JavaVM;
use log::{error, info};

const MINECRAFT_PACKAGE: &str = "com.mojang.minecraftpe";

pub fn try_launch(app: &android_activity::AndroidApp) {
    let ptr = app.vm_as_ptr();
    if ptr.is_null() {
        error!("JavaVM недоступна");
        return;
    }
    
    let vm = unsafe { JavaVM::from_raw(ptr as *mut jni::sys::JavaVM) };
    if let Ok(mut env) = vm.get_env() {
        if let Err(e) = launch(&mut env) {
            error!("Ошибка запуска: {e}");
        }
    }
}

fn launch(env: &mut jni::JNIEnv) -> Result<(), String> {
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

    let activity_class = env.find_class("org/kivy/android/PythonActivity")
        .or_else(|_| env.find_class("org/libsdl/app/SDLActivity"))
        .map_err(|e| e.to_string())?;

    let activity = env
        .get_static_field(activity_class, "mActivity", "Landroid/app/Activity;")
        .or_else(|_| env.get_static_field(env.find_class("org/libsdl/app/SDLActivity").unwrap(), "mCurrentContext", "Landroid/content/Context;"))
        .map_err(|e| e.to_string())?
        .l()
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
