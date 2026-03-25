package cards;

import gamecore.*;

/**
 * 示例卡牌：战车
 * 文件：战车.java
 */
public class 战车 extends 卡牌 {
    战车() {
        名称 = "战车";
        描述 = "无消耗，获得3点加速（额外出牌机会）";
        是否无消耗 = true;
        是否被动 = false;
    }
    
    @Override
    public void 使用效果(玩家 出牌者, 玩家 目标) {
        词条.施加加速(目标, 3, this);
    }
}