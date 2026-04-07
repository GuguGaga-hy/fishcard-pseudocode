package gamecore;

import cards.回避;
import java.util.*;

public class 玩家 {
    // ============================== 数值（先天+后天）==============================
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
    
    // ============================== 状态 ==============================
    public int 当前血量;
    public int 血量上限;
    public int 手牌上限 = 3;
    public List<卡牌> 手牌 = new ArrayList<>();
    public List<增益> 增益记录 = new ArrayList<>();

    // ============================== 效果 ==============================

    // 正面效果
    public int 铁壁计数 = 0;
    public int 神谕计数 = 0;
    public int 加速计数;
    
    // 负面效果
    public int 中毒计数;
    public int 诅咒计数;
    public int 魅惑计数;
    public int 狂暴计数;
    public int 冰冻计数;

    // 中性效果
    public int 忽略计数 = 0;
    private int 待施加不可选中计数 = 0;   // 延迟施加暂存
    public boolean 脱战 = false;           // 暂未实现
    private int 脱战计数 = 0;   // 剩余脱战窗口数，>0 表示处于脱战状态

    // 设置计数
    public void 设置中毒计数(int 计数) { 中毒计数 = Math.min(10, Math.max(0, 计数)); }
    public void 设置诅咒计数(int 计数) { 诅咒计数 = Math.min(10, Math.max(0, 计数)); }
    public void 设置魅惑计数(int 计数) { 魅惑计数 = Math.min(10, Math.max(0, 计数)); }
    public void 设置狂暴计数(int 计数) { 狂暴计数 = Math.min(10, Math.max(0, 计数)); }
    public void 设置冰冻计数(int 计数) { 冰冻计数 = Math.min(10, Math.max(0, 计数)); }
    public void 设置加速计数(int 计数) { 加速计数 = Math.min(10, Math.max(0, 计数)); }
    public void 设置铁壁计数(int 计数) { 铁壁计数 = Math.min(10, Math.max(0, 计数)); }
    public void 设置神谕计数(int 计数) { 神谕计数 = Math.min(10, Math.max(0, 计数)); }
    public void 设置忽略计数(int 计数) { 忽略计数 = Math.min(10, Math.max(0, 计数)); }
    public void 设置脱战计数(int 剩余) { 脱战计数 = Math.max(0, 剩余); }

    
    // 增加计数方法
    public void 增加铁壁计数(int 计数) { 设置铁壁计数(铁壁计数 + 计数); }
    public void 增加神谕计数(int 计数) { 设置神谕计数(神谕计数 + 计数); }
    public void 增加冰冻计数(int 计数) { 设置冰冻计数(冰冻计数 + 计数); }
    public void 增加忽略计数(int 计数) { 设置忽略计数(忽略计数 + 计数); }
    
    // 延迟施加
    public void 设置延迟忽略计数(int 计数) {
        待施加不可选中计数 = Math.min(10, Math.max(0, 计数));
    }
    public void 延迟增加忽略计数() {
        if (待施加不可选中计数 > 0) {
            增加忽略计数(待施加不可选中计数);
            待施加不可选中计数 = 0;
        }
    }
    
    // ============================== 行动辅助 ==============================
    public int 基础出牌机会;
    public 游戏 所属游戏;
    private boolean 主动结束行动 = false;   // 主动结束行动窗口的标志
    private boolean 跳过行动阶段 = false;   //因冰冻效果导致跳过行动窗口的标志
    
    // 抽牌与补牌
    public void 抽牌() {
        卡牌 新牌 = 所属游戏.从抽牌堆抽一张();
        if (新牌 != null) 手牌.add(新牌);
    }
    public void 补满手牌() {
        while (手牌.size() < 手牌上限) 抽牌();
    }
    
    // 脱战控制
    public int 获取脱战剩余窗口数() {
        return 脱战计数;
    }
    // 判断是否处于脱战状态
    public boolean 是否脱战中() {
        return 脱战计数 > 0;
    }
    
    // 判断是否不可被他人选中
    public boolean 是否不可被他人选中() {
        return 忽略计数 > 0 || 脱战计数 > 0;   // 脱战期间也不可被选中
    }
    // 不可选中倒计时
    public void 全局清除不可选中() {
        if (忽略计数 > 0) {
            忽略计数--;
        }
    }
    // ============================== 玩家动作 ==============================
    
    // 智消耗
    public boolean 消耗智换牌(卡牌 要弃的牌) {
        if (脱战计数 > 0) return false;   // 脱战中不能行动
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
        if (脱战计数 > 0) return false;
        int 总敏 = get敏();
        if (总敏 < 消耗量) return false;
        int 消耗后天 = Math.min(后天敏, 消耗量);
        后天敏 -= 消耗后天;
        int 消耗先天 = 消耗量 - 消耗后天;
        先天敏 -= 消耗先天;
        return true;
    }
    
    // 行动窗口
    
    // 强制结束当前行动窗口（供卡牌使用）
    public void 强制结束行动() {
        主动结束行动 = true;
    }

    public void 执行行动窗口() {
        // 脱战：跳过整个行动窗口
        if (脱战计数 > 0) {
            脱战计数--;
            return;   // 不执行任何阶段
        }
        开始判定阶段();
        玩家行动阶段();
        结束判定阶段();
    }
    
    public void 开始判定阶段() {
        // 中毒
        if (中毒计数 > 0) {
            词条.modify(this, -1, 0, null);
            中毒计数--;
            if (中毒计数 < 0) 中毒计数 = 0;
        }
        // 神谕
        if (神谕计数 > 0) {
            神谕计数--;
            int 新血量 = Math.min(血量上限, 当前血量 + 1);
            当前血量 = 新血量;
        }
        // 冰冻：若有计数，设置跳过行动标志（不移除计数，移除在结束阶段）
        if (冰冻计数 > 0) {
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
        主动结束行动 = false;  // 重置主动结束标志
        while ((基础出牌机会 > 0 || 加速计数 > 0) && !主动结束行动) {
            行动选择 选择 = 获取玩家选择();
            if (选择.类型 == 行动类型.出牌) {
                卡牌 牌 = 选择.卡牌;
                if (牌.是否被动) continue;
                
                玩家 实际目标 = 选择.目标;
                if (牌.是否攻击牌) {
                    if (魅惑计数 > 0) {
                        实际目标 = this;
                    } else if (狂暴计数 > 0) {
                        if (Math.random() < 0.5) 实际目标 = this;
                    }
                }
                // 检查最终目标是否可被他人选中（自己打自己不受限）
                if (实际目标 != this && 实际目标.是否不可被他人选中()) {
                    continue; // 目标不可选中，本次出牌无效，重新选择
                }
                
                牌.使用效果(this, 实际目标);
                if (基础出牌机会 > 0) {
                    基础出牌机会--;
                } else if (加速计数 > 0) {
                    加速计数--;
                }
                手牌.remove(牌);
                补满手牌();
            } else if (选择.类型 == 行动类型.使用技能) {
                // 技能暂未实现
            } else if (选择.类型 == 行动类型.结束行动) {
                主动结束行动 = true;  // 标记玩家主动结束行动
            }
        }
    }
    
    public void 增加加速计数(int 计数) {
        int 新计数 = 加速计数 + 计数;
        加速计数 = Math.min(10, Math.max(0, 新计数));
    }
    
    public void 结束判定阶段() {
        if (魅惑计数 > 0) 魅惑计数--;
        if (狂暴计数 > 0) 狂暴计数--;
        if (冰冻计数 > 0) 冰冻计数--;
        加速计数 = 0;
        if (当前血量 <= 0) 死亡();
    }
    
    public boolean 是否主动结束行动() {
        return 主动结束行动;
    }
    
    public void 死亡() { }
    
    // ==================== 被动响应 ====================
    public boolean 尝试响应被动(玩家 攻击方, 伤害类型 伤害类型) {
        if (脱战计数 > 0) return false;   // 脱战中无法响应
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