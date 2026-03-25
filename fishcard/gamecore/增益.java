package gamecore;

/**
 * 增益记录类
 * 仅用于记录属性增幅的数值变化，供净化时追溯
 */
public class 增益 {
    public int 攻增幅, 防增幅, 魔增幅, 抗增幅, 敏增幅, 智增幅;
    
    public 增益(int 攻, int 防, int 魔, int 抗, int 敏, int 智) {
        this.攻增幅 = 攻;
        this.防增幅 = 防;
        this.魔增幅 = 魔;
        this.抗增幅 = 抗;
        this.敏增幅 = 敏;
        this.智增幅 = 智;
    }
}