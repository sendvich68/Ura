use android_activity::{AndroidApp, MainEvent, PollEvent};
use android_logger::Config;
use log::LevelFilter;

mod config;
mod game;
mod ui;
mod overlay;

#[no_mangle]
fn android_main(app: AndroidApp) {
    android_logger::init_once(
        Config::default()
            .with_max_level(LevelFilter::Info)
            .with_tag("StanLauncher"),
    );

    log::info!("Stan Launcher запущен");
    let cfg = config::Config::load();
    let mut app_loop = ui::AppLoop::new(app, cfg);
    app_loop.run();
}
