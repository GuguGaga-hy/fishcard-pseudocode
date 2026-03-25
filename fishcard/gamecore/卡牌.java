package gamecore;

/**
 * 卡牌抽象基类
 */
public abstract class 卡牌 {
    public String 名称;
    public String 描述;
    public boolean 是否无消耗;
    public boolean 是否被动;
    
    // 词条标签
    public boolean 是否攻击牌 = false;
    public boolean 是否增幅 = false;
    public boolean 是否治愈 = false;
    public boolean 是否净化 = false;
    public boolean 是否铁壁 = false;
    public boolean 是否神谕 = false;
    public boolean 是否加速 = false;
    public boolean 是否中毒 = false;
    public boolean 是否诅咒 = false;
    public boolean 是否魅惑 = false;
    public boolean 是否狂暴 = false;
    public boolean 是否冰冻 = false;
    public boolean 是否修改 = false;
    
    public abstract void 使用效果(玩家 出牌者, 玩家 目标);
}