
package custom.droid.animation;

public class AnimationObject {

    private float x = 0;

    private float y = 0;

    private float scaleX = 0;

    private float scaleY = 0;

    private int width = 0;

    private int height = 0;

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getScaleX() {
        return scaleX;
    }

    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("AnimationObject [x : " + x);
        sb.append(", y : " + y);
        sb.append(", width : " + width);
        sb.append(", height : " + height);
        sb.append(", scaleX : " + scaleX);
        sb.append(", scaleY : " + scaleY);
        sb.append("]");

        return sb.toString();
    }
}
