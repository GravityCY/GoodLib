package me.gravityio.goodlib.util;

import net.minecraft.util.math.Vec3d;

/**
 * Makes it easy to check if a player has moved a certain distance<br>
 * Use {@link MoveUpdater#tick(Vec3d) tick} if you want to just get the distance the player has moved every tick <br>
 * If not just manually use the rest of the functions
 */
public class MoveUpdater {
    private Vec3d origin;
    private Vec3d pos;

    public double tick(Vec3d pos) {
        if (this.origin == null) this.origin = pos;
        return getDistance(this.origin, pos);
    }

    public void setOrigin(Vec3d pos) {
        this.origin = pos;
    }

    public void setOrigin() {
        this.origin = this.pos;
    }

    public void setPos(Vec3d pos) {
        if (this.origin == null)
            this.origin = pos;
        this.pos = pos;
    }

    public double getDistance() {
        return getDistance(this.origin, this.pos);
    }

    private double getDistance(Vec3d a, Vec3d b) {
        return Math.abs(a.subtract(b).horizontalLength());
    }

}
