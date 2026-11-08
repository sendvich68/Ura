use android_activity::{AndroidApp, MainEvent, PollEvent, InputStatus};
use egui::{Context, CentralPanel, RichText, Button};
use crate::config::Config;
use crate::game;

pub struct AppLoop {
    app: AndroidApp,
    cfg: Config,
    egui_ctx: Context,
    raw_input: egui::RawInput,
}

impl AppLoop {
    pub fn new(app: AndroidApp, cfg: Config) -> Self {
        Self {
            app,
            cfg,
            egui_ctx: Context::default(),
            raw_input: egui::RawInput::default(),
        }
    }

    pub fn run(&mut self) {
        loop {
            // Ультра-фикс: poll_events принимает замыкание и НИЧЕГО не возвращает
            self.app.poll_events(Some(std::time::Duration::from_millis(16)), |event| {
                match event {
                    PollEvent::Main(MainEvent::WindowResized { width, height }) => {
                        self.raw_input.screen_rect = Some(egui::Rect::from_min_size(
                            egui::Pos2::ZERO,
                            egui::vec2(width as f32, height as f32),
                        ));
                    }
                    PollEvent::Main(MainEvent::InputAvailable) => {
                        if let Ok(mut iter) = self.app.input_events_iter() {
                            loop {
                                let read_input = iter.next(|input_event| {
                                    use android_activity::input::{InputEvent, MotionAction};
                                    if let InputEvent::MotionEvent(m) = input_event {
                                        for pointer in m.pointers() {
                                            let pos = egui::pos2(pointer.x(), pointer.y());
                                            if matches!(m.action(), MotionAction::Down) {
                                                self.raw_input.events.push(egui::Event::PointerMoved(pos));
                                                self.raw_input.events.push(egui::Event::PointerButton {
                                                    pos, button: egui::PointerButton::Primary, pressed: true, modifiers: Default::default(),
                                                });
                                            }
                                            if matches!(m.action(), MotionAction::Up) {
                                                self.raw_input.events.push(egui::Event::PointerButton {
                                                    pos, button: egui::PointerButton::Primary, pressed: false, modifiers: Default::default(),
                                                });
                                            }
                                        }
                                    }
                                    InputStatus::Handled
                                });

                                if !read_input {
                                    break;
                                }
                            }
                        }
                    }
                    PollEvent::Main(MainEvent::RedrawNeeded) | PollEvent::Timeout => {
                        self.draw();
                    }
                    PollEvent::Main(MainEvent::Destroy) => {
                        std::process::exit(0);
                    }
                    _ => {}
                }
            }); // <--- ТОЧКА С ЗАПЯТОЙ ЗАКРЫВАЕТ ВЫЗОВ poll_events
        }
    }

    fn draw(&mut self) {
        self.raw_input.time = Some(std::time::SystemTime::now().duration_since(std::time::UNIX_EPOCH).map(|d| d.as_secs_f64()).unwrap_or(0.0));

        let _ = self.egui_ctx.run(self.raw_input.take(), |ctx| {
            CentralPanel::default().show(ctx, |ui| {
                ui.vertical_centered(|ui| {
                    ui.add_space(20.0);
                    ui.heading(RichText::new("Stan Launcher").size(36.0).strong());
                    ui.label(RichText::new("Minecraft Bedrock Edition").size(18.0));
                    ui.add_space(20.0);

                    if ui.add_sized([260.0, 50.0], Button::new("▶  Играть (v1.8)")).clicked() {
                        game::try_launch(&self.app);
                    }
                    
                    ui.add_space(15.0);
                    ui.horizontal(|ui| {
                        ui.label("Версия: 1.8");
                        ui.label(" | Моды: 0");
                        ui.label(" | Ресурспаки: 0");
                        ui.label(" | Миры: 0");
                    });
                    
                    ui.add_space(15.0);
                    ui.label(format!("Прозрачность кнопок: {:.1}", self.cfg.controls.transparency));
                    ui.label(format!("Размер джойстика: {}", self.cfg.controls.joy_size));
                });
            });
        });
    }
}
