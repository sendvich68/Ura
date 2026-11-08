use serde::{Deserialize, Serialize};
use std::fs;

pub const CONFIG_PATH: &str = "stan_config.json";

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Controls {
    pub joy_size: i32,
    pub btn_jump_size: i32,
    pub transparency: f32,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
pub struct Config {
    pub version: String,
    pub controls: Controls,
}

impl Default for Config {
    fn default() -> Self {
        Self {
            version: "1.8".into(),
            controls: Controls {
                joy_size: 120,
                btn_jump_size: 80,
                transparency: 0.6,
            },
        }
    }
}

impl Config {
    pub fn load() -> Self {
        fs::read_to_string(CONFIG_PATH)
            .ok()
            .and_then(|s| serde_json::from_str(&s).ok())
            .unwrap_or_default()
    }

    pub fn save(&self) {
        if let Ok(s) = serde_json::to_string_pretty(self) {
            let _ = fs::write(CONFIG_PATH, s);
        }
    }
}
