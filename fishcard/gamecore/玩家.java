package gamecore;

import cards.回避;
import java.util.*;

public class 玩家 {
    // 数值（先天+后天）
    public int 先天攻, 后天攻;
    public int 先天防, 后天防;
    public int 先天魔, 后天魔;
    public int 先天抗, 后天抗;
    public int 先天敏, 后天敏;
    public int 先天智, 后天智;
    public int 速;
    
    public int get攻() { return 先天攻 + 后天攻; }
    public int get防() { return 先天防 + 后天防; }
    public int get魔() { return 先天魔 + 后天魔; }
    public int get抗() { return 先天抗 + 后天抗; }
    public int get敏() { return 先天敏 + 后天敏; }
    public int get智() { return 先天智 + 后天智; }
    
    // 状态
    public int 当前血量;
    public int 血量上限;
    public int 手牌上限 = 3;
    public List<卡牌> 手牌 = new ArrayList<>();
    
    public List<增益> 增益记录 = new ArrayList<>();
    public int 铁壁层数 = 0;
    public int 神谕层数 = 0;
    
    // 负面层数
    public int 中毒层数;
    public int 诅咒层数;
    public int 魅惑层数;
    public int 狂暴层数;
    public int 冰冻层数;
    
    // 加速层数
    public int 加速层数;
    
    // 行动资源
    public int 基础出牌机会;
    
    // 游戏引用
    public 游戏 所属游戏;
    
    // 主动结束标志
    private boolean 主动结束 = false;
    // 冰冻跳过标志
    private boolean 跳过行动阶段 = false;
    
    // 抽牌与补牌
    public void 抽牌() {
        卡牌 新牌 = 所属游戏.从抽牌堆抽一张();
        if (新牌 != null) 手牌.add(新牌);
    }
    
    public void 补满手牌() {
        while (手牌.size() < 手牌上限) 抽牌();
    }
    
    // 智消耗
    public boolean 消耗智换牌(卡牌 要弃的牌) {
        if (get智() < 1) return false;
        int 消耗后天 = Math.min(后天智, 1);
        后天智 -= 消耗后天;
        int 消耗先天 = 1 - 消耗后天;
        先天智 -= 消耗先天;
        if (手牌.remove(要弃的牌)) {
            所属游戏.将牌放入弃牌堆(要弃的牌);
            抽牌();
            return true;
        }
        return false;
    }
    
    // 敏消耗
    public boolean 消耗敏(int 消耗量) {
        int 总敏 = get敏();
        if (总敏 < 消耗量) return false;
        int 消耗后天 = Math.min(后天敏, 消耗量);
        后天敏 -= 消耗后天;
        int 消耗先天 = 消耗量 - 消耗后天;
        先天敏 -= 消耗先天;
        return true;
    }
    
    // 设置层数
    public void 设置中毒层数(int 层数) { 中毒层数 = Math.min(10, Math.max(0, 层数)); }
    public void 设置诅咒层数(int 层数) { 诅咒层数 = Math.min(10, Math.max(0, 层数)); }
    public void 设置魅惑层数(int 层数) { 魅惑层数 = Math.min(10, Math.max(0, 层数)); }
    public void 设置狂暴层数(int 层数) { 狂暴层数 = Math.min(10, Math.max(0, 层数)); }
    public void 设置冰冻层数(int 层数) { 冰冻层数 = Math.min(10, Math.max(0, 层数)); }
    public void 设置加速层数(int 层数) { 加速层数 = Math.min(10, Math.max(0, 层数)); }
    public void 设置铁壁层数(int 层数) { 铁壁层数 = Math.min(10, Math.max(0, 层数)); }
    public void 设置神谕层数(int 层数) { 神谕层数 = Math.min(10, Math.max(0, 层数)); }
    
    // 增加层数方法
    public void 增加铁壁层数(int 层数) { 设置铁壁层数(铁壁层数 + 层数); }
    public void 增加神谕层数(int 层数) { 设置神谕层数(神谕层数 + 层数); }
    public void 增加冰冻层数(int 层数) { 设置冰冻层数(冰冻层数 + 层数); }
    
    // 行动窗口
    public void 执行行动窗口() {
        开始判定阶段();
        玩家行动阶段();
        结束判定阶段();
    }
    
    public void 开始判定阶段() {
        // 中毒
        if (中毒层数 > 0) {
            词条.modify(this, -1, 0, null);
            中毒层数--;
            if (中毒层数 < 0) 中毒层数 = 0;
        }
        // 神谕
        if (神谕层数 > 0) {
            神谕层数--;
            int 新血量 = Math.min(血量上限, 当前血量 + 1);
            当前血量 = 新血量;
        }
        // 冰冻：若有层数，设置跳过行动标志（不移除层数，移除在结束阶段）
        if (冰冻层数 > 0) {
            跳过行动阶段 = true;
        }
        // 其他开始判定效果可在此扩展...
    }
    
    public void 玩家行动阶段() {
        // 如果因冰冻跳过，则直接返回（不执行任何行动）
        if (跳过行动阶段) {
            跳过行动阶段 = false;  // 重置标志，避免影响下一回合
            return;
        }
        基础出牌机会 = 1;
        主动结束 = false;  // 重置主动结束标志
        while ((基础出牌机会 > 0 || 加速层数 > 0) && !主动结束) {
            行动选择 选择 = 获取玩家选择();
            if (选择.类型 == 行动类型.出牌) {
                卡牌 牌 = 选择.卡牌;
                if (牌.是否被动) continue;
                
                玩家 实际目标 = 选择.目标;
                if (牌.是否攻击牌) {
                    if (魅惑层数 > 0) {
                        实际目标 = this;
                    } else if (狂暴层数 > 0) {
                        if (Math.random() < 0.5) 实际目标 = this;
                    }
                }
                
                牌.使用效果(this, 实际目标);
                if (基础出牌机会 > 0) {
                    基础出牌机会--;
                } else if (加速层数 > 0) {
                    加速层数--;
                }
                手牌.remove(牌);
                补满手牌();
            } else if (选择.类型 == 行动类型.使用技能) {
                // 技能暂未实现
            } else if (选择.类型 == 行动类型.结束行动) {
                主动结束 = true;  // 标记玩家主动结束行动
            }
        }
    }
    
    public void 增加加速层数(int 层数) {
        int 新层数 = 加速层数 + 层数;
        加速层数 = Math.min(10, Math.max(0, 新层数));
    }
    
    public void 结束判定阶段() {
        if (魅惑层数 > 0) 魅惑层数--;
        if (狂暴层数 > 0) 狂暴层数--;
        if (冰冻层数 > 0) 冰冻层数--;
        加速层数 = 0;
        if (当前血量 <= 0) 死亡();
    }
    
    public boolean 是否主动结束行动() {
        return 主动结束;
    }
    
    public void 死亡() { }
    
    // ==================== 被动响应 ====================
    public boolean 尝试响应被动(玩家 攻击方, 伤害类型 伤害类型) {
        List<卡牌> 可用回避牌 = new ArrayList<>();
        for (卡牌 牌 : 手牌) {
            if (牌 instanceof 回避 && 牌.是否被动) {
                可用回避牌.add(牌);
            }
        }
        boolean 有回避牌 = !可用回避牌.isEmpty();
        boolean 有敏 = (get敏() > 0);
        
        if (!有回避牌 && !有敏) {
            return false;
        }
        
        int 选择 = 询问玩家是否使用回避(有回避牌, 有敏);
        
        if (选择 == 0) {
            return false;
        } else if (选择 == 1 && 有回避牌) {
            卡牌 回避牌 = 可用回避牌.get(0);
            回避牌.使用效果(this, this);
            手牌.remove(回避牌);
            补满手牌();
            return true;
        } else if (选择 == 2 && 有敏) {
            消耗敏(1);
            return true;
        } else {
            return false;
        }
    }
    
    private int 询问玩家是否使用回避(boolean 有回避牌, boolean 有敏) {
        if (有回避牌) return 1;
        if (有敏) return 2;
        return 0;
    }
    
    // 辅助
    public 行动选择 获取玩家选择() { return new 行动选择(); }
    public boolean 是否存活() { return 当前血量 > 0; }
}

class 行动选择 {
    public 行动类型 类型;
    public 卡牌 卡牌;
    public 玩家 目标;
}