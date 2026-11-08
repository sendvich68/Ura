use crate::config::Controls;
use egui::{Color32, Pos2, Rect, Rounding, Vec2};

pub fn draw(ctx: &egui::Context, cfg: &Controls) -> bool {
    let mut pressed = false;
    let alpha = (cfg.transparency * 255.0) as u8;

    egui::Area::new("overlay".into())
        .fixed_pos(Pos2::ZERO)
        .show(ctx, |ui| {
            let painter = ui.painter();
            let rect = Rect::from_min_size(
                Pos2::new(100.0, 100.0),
                Vec2::splat(cfg.joy_size as f32),
            );
            painter.rect_filled(rect, Rounding::same(8.0), Color32::from_rgba_unmultiplied(120, 120, 120, alpha));

            if ui.rect_contains_pointer(rect) && ui.input(|i| i.pointer.primary_down()) {
                pressed = true;
            }
        });
    pressed
}