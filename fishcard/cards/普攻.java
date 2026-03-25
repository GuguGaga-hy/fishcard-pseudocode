package cards;

import gamecore.*;

/**
 * 示例卡牌：普攻
 * 文件：普攻.java
 */
public class 普攻 extends 卡牌 {
    普攻() {
        名称 = "普攻";
        描述 = "造成1点物理伤害";
        是否无消耗 = false;
        是否被动 = false;
        // 是否攻击牌将由词条.伤害自动标记
    }
    
    @Override
    public void 使用效果(玩家 出牌者, 玩家 目标) {
        出牌者.所属游戏.造成伤害(出牌者, 目标, 伤害类型.物理伤害, 1, this);
    }
}