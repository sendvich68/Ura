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
    
    // Прикрепляем текущий поток к Java
    let mut env = match vm.attach_current_thread() {
        Ok(e) => e,
        Err(e) => {
            error!("Не удалось прикрепить поток к JVM: {}", e);
            return;
        }
    };

    if let Err(e) = launch(&mut env) {
        error!("Ошибка запуска Minecraft: {}", e);
    }
}

fn launch(env: &mut jni::JNIEnv) -> Result<(), String> {
    // 1. Создаем Intent
    let action = env.new_string("android.intent.action.MAIN").unwrap();
    let intent = env
        .new_object(
            "android/content/Intent",
            "(Ljava/lang/String;)V",
            &[JValue::Object(&JObject::from(action))],
        )
        .map_err(|e| format!("Ошибка создания Intent: {}", e))?;

    // 2. Устанавливаем пакет Minecraft
    let pkg = env.new_string(MINECRAFT_PACKAGE).unwrap();
    env.call_method(
        &intent,
        "setPackage",
        "(Ljava/lang/String;)Landroid/content/Intent;",
        &[JValue::Object(&JObject::from(pkg))],
    )
    .map_err(|e| format!("Ошибка setPackage: {}", e))?;

    // 3. Получаем текущую Activity (без сложных проверок)
    let activity_class = env
        .find_class("org/libsdl/app/SDLActivity")
        .or_else(|_| env.find_class("org/kivy/android/PythonActivity"))
        .map_err(|e| format!("Не найден класс Activity: {}", e))?;

    let m_activity = env
        .get_static_field(&activity_class, "mActivity", "Landroid/app/Activity;")
        .or_else(|_| env.get_static_field(&activity_class, "mCurrentContext", "Landroid/content/Context;"))
        .map_err(|e| format!("Не найдено поле Activity: {}", e))?;

    let activity_obj = m_activity
        .l()
        .map_err(|e| format!("Ошибка получения Activity: {}", e))?;

    // 4. Запускаем игру!
    env.call_method(
        &activity_obj,
        "startActivity",
        "(Landroid/content/Intent;)V",
        &[JValue::Object(&intent)],
    )
    .map_err(|e| format!("Ошибка startActivity: {}", e))?;

    info!("✅ Minecraft запущен!");
    Ok(())
}
