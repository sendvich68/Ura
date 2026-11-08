use horizon_api::{
    block::{Block, BlockBehavior, BlockId, BlockProperties, SoundType},
    mod_prelude::*,
    registry::Registry,
};

/// Упрочнённое стекло: прозрачное, но с прочностью камня (не ломается с одного удара)
pub struct ReinforcedGlass;

impl BlockBehavior for ReinforcedGlass {
    fn properties(&self) -> BlockProperties {
        BlockProperties::builder()
            .hardness(1.5)          // обычное стекло ~0.3, камень 1.5
            .resistance(6.0)        // взрывоустойчивость (как у камня)
            .transparent(true)
            .sound(SoundType::Glass)
            .requires_tool(true)    // ломается только киркой
            .build()
    }

    fn on_break(&self, _ctx: &mut BlockBreakContext) {
        // Можно добавить дроп: например, выпадает само стекло
    }
}

#[horizon::mod_init]
pub fn init(registry: &mut Registry) -> Result<(), ModError> {
    let id = BlockId::new("reinforced_glass", "reinforced_glass");

    registry.register_block(
        id,
        ReinforcedGlass,
        // привязка текстур и модели
        BlockModel::cube_all("reinforced_glass:block/reinforced_glass"),
    )?;

    Ok(())
}
