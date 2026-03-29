package gamecore;

import java.util.*;

public class 游戏 {
    public List<玩家> 玩家列表;
    public List<玩家> 固定行动顺序;
    public int 当前回合数;
    public List<卡牌> 抽牌堆;
    public List<卡牌> 弃牌堆;
    
    public void 开始游戏() {
        初始化玩家();
        确定固定行动顺序();
        while (!是否游戏结束()) {
            for (玩家 当前玩家 : 固定行动顺序) {
                if (当前玩家.是否存活()) {
                    // 全局清除不可选中：每个行动窗口开始时，所有存活玩家的不可选中层数减1
                    for (玩家 p : 玩家列表) {
                        p.全局清除不可选中();
                    }
                    当前玩家.执行行动窗口();
                }
            }
            当前回合数++;
        }
        宣布胜者();
    }
    
    public void 确定固定行动顺序() {
        List<玩家> 顺序 = new ArrayList<>(玩家列表);
        顺序.sort((p1, p2) -> Integer.compare(p2.速, p1.速));
        for (int i = 0; i < 顺序.size() - 1; i++) {
            if (顺序.get(i).速 == 顺序.get(i+1).速) {
                if (石头剪刀布(顺序.get(i), 顺序.get(i+1)) == 顺序.get(i+1)) {
                    Collections.swap(顺序, i, i+1);
                }
            }
        }
        固定行动顺序 = 顺序;
    }
    
    public 玩家 石头剪刀布(玩家 a, 玩家 b) { return a; }
    
    public List<玩家> 获取存活玩家() {
        List<玩家> 存活 = new ArrayList<>();
        for (玩家 p : 玩家列表) if (p.当前血量 > 0) 存活.add(p);
        return 存活;
    }
    
    public boolean 是否游戏结束() { return 获取存活玩家().size() <= 1; }
    
    public void 宣布胜者() {
        List<玩家> 存活 = 获取存活玩家();
        if (存活.isEmpty()) System.out.println("平局？");
        else System.out.println("胜者：" + 存活.get(0));
    }
    
    public void 初始化玩家() {
        for (玩家 p : 玩家列表) {
            p.所属游戏 = this;
            p.补满手牌();
        }
    }
    
    public 卡牌 从抽牌堆抽一张() {
        if (抽牌堆.isEmpty()) return null;
        return 抽牌堆.remove(0);
    }
    
    public void 将牌放入弃牌堆(卡牌 牌) { 弃牌堆.add(牌); }
    
    public void 造成伤害(玩家 攻击方, 玩家 目标, 伤害类型 类型, int 基础值, 卡牌 来源) {
        if (目标.尝试响应被动(攻击方, 类型)) return;
        词条.伤害(攻击方, 目标, 类型, 基础值, 来源);
        // 伤害结算后，应用目标的延迟不可选中
        目标.应用延迟不可选中();
    }
}