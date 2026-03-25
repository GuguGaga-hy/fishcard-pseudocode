package cards;

import gamecore.*;

/**
 * 示例卡牌：加速术
 * 文件：加速术.java
 */
public class 加速术 extends 卡牌 {
    加速术() {
        名称 = "加速术";
        描述 = "无消耗，获得1点加速（额外出牌机会）";
        是否无消耗 = true;
        是否被动 = false;
    }
    
    @Override
    public void 使用效果(玩家 出牌者, 玩家 目标) {
        词条.施加加速(目标, 1, this);
    }
}