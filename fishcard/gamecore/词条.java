package gamecore;

/**
 * 卡牌效果词条库
 */
public class 词条 {
    
    // 增幅
    public static void 增幅(玩家 目标, int 攻增, int 防增, int 魔增, int 抗增, int 敏增, int 智增, 卡牌 来源) {
        if (来源 != null) 来源.是否增幅 = true;
        目标.后天攻 += 攻增;
        目标.后天防 += 防增;
        目标.后天魔 += 魔增;
        目标.后天抗 += 抗增;
        目标.后天敏 += 敏增;
        目标.后天智 += 智增;
        目标.增益记录.add(new 增益(攻增, 防增, 魔增, 抗增, 敏增, 智增));
    }
    
    // 伤害
    public static void 伤害(玩家 攻击方, 玩家 目标, 伤害类型 类型, int 基础值, 卡牌 来源) {
        if (来源 != null) 来源.是否攻击牌 = true;
        
        // 铁壁判定：若目标有铁壁层数，消耗一层并直接返回
        if (目标.铁壁层数 > 0) {
            目标.铁壁层数--;
            return;  // 本次伤害被铁壁完全抵消
        }
        
        int 最终伤害;
        switch (类型) {
            case 物理伤害:
                最终伤害 = Math.max(1, 基础值 + 攻击方.get攻() - 目标.get防());
                break;
            case 魔法伤害:
                最终伤害 = Math.max(1, 基础值 + 攻击方.get魔() - 目标.get抗());
                break;
            case 真实伤害:
                最终伤害 = 基础值;
                break;
            default:
                return;
        }
        
        if (目标.诅咒层数 > 0) {
            最终伤害 *= 2;
            目标.诅咒层数--;
        }
        
        目标.当前血量 = Math.max(0, 目标.当前血量 - 最终伤害);
    }
    
    // 治愈
    public static void 治愈(玩家 目标, int 治疗量, 卡牌 来源) {
        if (来源 != null) 来源.是否治愈 = true;
        目标.当前血量 = Math.min(目标.血量上限, 目标.当前血量 + 治疗量);
    }
    
    // 净化
    public static void 净化(玩家 目标, 卡牌 来源) {
        if (来源 != null) 来源.是否净化 = true;
        目标.后天攻 = 0;
        目标.后天防 = 0;
        目标.后天魔 = 0;
        目标.后天抗 = 0;
        目标.后天敏 = 0;
        目标.后天智 = 0;
        目标.铁壁层数 = 0;      // 清空铁壁
        目标.神谕层数 = 0;      // 清空神谕
        目标.增益记录.clear();
    }
    
    // 铁壁（支持层数）
    public static void 施加铁壁(玩家 目标, int 层数, 卡牌 来源) {
        if (来源 != null) 来源.是否铁壁 = true;
        目标.增加铁壁层数(层数);
    }
    
    // 神谕（支持层数）
    public static void 施加神谕(玩家 目标, int 层数, 卡牌 来源) {
        if (来源 != null) 来源.是否神谕 = true;
        目标.增加神谕层数(层数);
    }
    
    // 兼容旧调用（默认层数1）
    public static void 施加铁壁(玩家 目标, 卡牌 来源) {
        施加铁壁(目标, 1, 来源);
    }
    public static void 施加神谕(玩家 目标, 卡牌 来源) {
        施加神谕(目标, 1, 来源);
    }
    
    // 加速（支持层数）
    public static void 施加加速(玩家 目标, int 层数, 卡牌 来源) {
        if (来源 != null) 来源.是否加速 = true;
        目标.增加加速层数(层数);
    }
    
    // 负面效果通用方法
    private static void 施加负面效果(玩家 目标, 负面效果类型 类型, int 层数, 卡牌 来源) {
        if (来源 != null) {
            switch (类型) {
                case 中毒: 来源.是否中毒 = true; break;
                case 诅咒: 来源.是否诅咒 = true; break;
                case 魅惑: 来源.是否魅惑 = true; break;
                case 狂暴: 来源.是否狂暴 = true; break;
                case 冰冻: 来源.是否冰冻 = true; break;
            }
        }
        
        int 新层数;
        switch (类型) {
            case 中毒:
                新层数 = 目标.中毒层数 + 层数;
                目标.中毒层数 = Math.min(10, Math.max(0, 新层数));
                break;
            case 诅咒:
                新层数 = 目标.诅咒层数 + 层数;
                目标.诅咒层数 = Math.min(10, Math.max(0, 新层数));
                break;
            case 魅惑:
                新层数 = 目标.魅惑层数 + 层数;
                目标.魅惑层数 = Math.min(10, Math.max(0, 新层数));
                break;
            case 狂暴:
                新层数 = 目标.狂暴层数 + 层数;
                目标.狂暴层数 = Math.min(10, Math.max(0, 新层数));
                break;
            case 冰冻:
                新层数 = 目标.冰冻层数 + 层数;
                目标.冰冻层数 = Math.min(10, Math.max(0, 新层数));
                break;
        }
    }
    
    public static void 施加中毒(玩家 目标, int 层数, 卡牌 来源) {
        施加负面效果(目标, 负面效果类型.中毒, 层数, 来源);
    }
    
    public static void 施加诅咒(玩家 目标, int 层数, 卡牌 来源) {
        施加负面效果(目标, 负面效果类型.诅咒, 层数, 来源);
    }
    
    public static void 施加魅惑(玩家 目标, int 层数, 卡牌 来源) {
        施加负面效果(目标, 负面效果类型.魅惑, 层数, 来源);
    }
    
    public static void 施加狂暴(玩家 目标, int 层数, 卡牌 来源) {
        施加负面效果(目标, 负面效果类型.狂暴, 层数, 来源);
    }

    public static void 施加冰冻(玩家 目标, int 层数, 卡牌 来源) {
        施加负面效果(目标, 负面效果类型.冰冻, 层数, 来源);
    }
    
    // modify
    public static void modify(玩家 目标, int 血量变化, int 手牌变化, 卡牌 来源) {
        if (来源 != null) 来源.是否修改 = true;
        if (血量变化 != 0) {
            目标.当前血量 = Math.min(目标.血量上限, Math.max(0, 目标.当前血量 + 血量变化));
        }
        if (手牌变化 > 0) {
            for (int i = 0; i < 手牌变化; i++) 目标.抽牌();
        } else if (手牌变化 < 0) {
            for (int i = 0; i < -手牌变化; i++) {
                if (!目标.手牌.isEmpty()) {
                    卡牌 弃牌 = 目标.手牌.remove(目标.手牌.size() - 1);
                    目标.所属游戏.将牌放入弃牌堆(弃牌);
                }
            }
        }
    }

    // 不可选中（中性效果）
    public static void 施加不可选中(玩家 目标, int 层数, 卡牌 来源) {
        // 中性效果不设置卡牌标签
        目标.增加不可选中层数(层数);
    }

    // 延迟施加不可选中（用于隐匿等）
    public static void 延迟施加不可选中(玩家 目标, int 层数, 卡牌 来源) {
        目标.设置待施加不可选中层数(层数);
    }
}