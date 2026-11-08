use android_activity::{AndroidApp, MainEvent, PollEvent};
use egui::Context;

use crate::config::Config;
use crate::game;
use crate::overlay;

pub struct AppLoop {
    app: AndroidApp,
    cfg: Config,
    show_overlay: bool,
    egui_ctx: Context,
    raw_input: egui::RawInput,
}

impl AppLoop {
    pub fn new(app: AndroidApp, cfg: Config) -> Self {
        Self {
            app,
            cfg,
            show_overlay: false,
            egui_ctx: Context::default(),
            raw_input: egui::RawInput::default(),
        }
    }

    pub fn run(&mut self) {
        loop {
            match self.app.poll_events(Some(std::time::Duration::from_millis(16))) {
                PollEvent::Main(MainEvent::InitWindow { .. }) => {
                    log::info!("Окно создано");
                }
                PollEvent::Main(MainEvent::WindowResized { width, height }) => {
                    self.raw_input.screen_rect = Some(egui::Rect::from_min_size(
                        egui::Pos2::ZERO,
                        egui::vec2(width as f32, height as f32),
                    ));
                }
                PollEvent::Main(MainEvent::InputAvailable) => {
                    let _ = self.app.input_events_iter().read(|event| {
                        use android_activity::input::{InputEvent, MotionAction};
                        if let InputEvent::MotionEvent(m) = event {
                            let pos = egui::pos2(m.pointer_coords(0).0, m.pointer_coords(0).1);
                            let down = matches!(m.action(), MotionAction::Down);
                            let up = matches!(m.action(), MotionAction::Up);

                            if down {
                                self.raw_input.events.push(egui::Event::PointerMoved(pos));
                                self.raw_input.events.push(egui::Event::PointerButton {
                                    pos, button: egui::PointerButton::Primary, pressed: true, modifiers: Default::default(),
                                });
                            }
                            if up {
                                self.raw_input.events.push(egui::Event::PointerButton {
                                    pos, button: egui::PointerButton::Primary, pressed: false, modifiers: Default::default(),
                                });
                            }
                        }
                    });
                }
                PollEvent::Main(MainEvent::RedrawNeeded) | PollEvent::Timeout => {
                    self.draw();
                }
                PollEvent::Main(MainEvent::Destroy) => break,
                _ => {}
            }
        }
    }

    fn draw(&mut self) {
        self.raw_input.time = Some(std::time::SystemTime::now().duration_since(std::time::UNIX_EPOCH).map(|d| d.as_secs_f64()).unwrap_or(0.0));

        let _ = self.egui_ctx.run(self.raw_input.take(), |ctx| {
            egui::CentralPanel::default().show(ctx, |ui| {
                ui.vertical_centered(|ui| {
                    ui.add_space(40.0);
                    ui.heading(egui::RichText::new("Stan Launcher").size(36.0).strong());
                    ui.label(egui::RichText::new("Minecraft Bedrock v1.8").size(18.0));
                    ui.add_space(30.0);

                    if ui.add_sized([260.0, 56.0], egui::Button::new("▶  Играть")).clicked() {
                        game::try_launch(&self.app);
                    }
                    ui.add_space(12.0);

                    if ui.add_sized([260.0, 48.0], egui::Button::new("⚙  Настройки")).clicked() {
                        self.show_overlay = !self.show_overlay;
                    }
                });
            });

            if self.show_overlay {
                overlay::draw(ctx, &self.cfg.controls);
            }
        });
    }
}
