package cards;

import gamecore.*;

/**
 * 通用伤害卡（通过组合词条实现）
 * 文件：伤害卡.java
 */
public class 伤害卡 extends 卡牌 {
    伤害类型 类型;
    int 基础伤害;
    
    伤害卡(String 名, String 描述, 伤害类型 类型, int 基础伤害) {
        this.名称 = 名;
        this.描述 = 描述;
        this.类型 = 类型;
        this.基础伤害 = 基础伤害;
        this.是否无消耗 = false;
        this.是否被动 = false;
        // 是否攻击牌将由词条.伤害自动标记
    }
    
    @Override
    public void 使用效果(玩家 出牌者, 玩家 目标) {
        // 通过游戏实例调用造成伤害，以触发被动响应并传递来源卡牌
        出牌者.所属游戏.造成伤害(出牌者, 目标, 类型, 基础伤害, this);
    }
}