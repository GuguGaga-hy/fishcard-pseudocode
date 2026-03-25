package cards;

import gamecore.*;

/**
 * 示例被动卡牌：回避
 * 文件：回避.java
 */
public class 回避 extends 卡牌 {
    回避() {
        名称 = "回避";
        描述 = "无视一次攻击（可在他人回合中响应打出）";
        是否无消耗 = true;
        是否被动 = true;
    }

    @Override
    public void 使用效果(玩家 出牌者, 玩家 目标) {
        // 回避效果已由玩家.尝试响应被动中的逻辑实现，此处无需额外操作
        // 可留空，或保留记录日志等非功能性代码
    }
}