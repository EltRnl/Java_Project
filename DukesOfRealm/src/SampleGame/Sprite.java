package SampleGame;

import java.io.Serializable;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

public abstract class Sprite implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7177303647744304431L;

	private ImageView imageView;

    protected Pane layer;

    protected int x;
    protected int y;

    protected int dx;
    protected int dy;

    private boolean removable = false;

    private double w;
    private double h;

    public Sprite(Pane layer, Image image, int x, int y) {

        this.layer = layer;
        this.x = x;
        this.y = y;

        this.w = image.getWidth(); 
        this.h = image.getHeight(); 
        
        this.imageView = new ImageView(image);
        this.imageView.relocate(x-w/2, y-h/2);

        addToLayer();

    }

    public void addToLayer() {
        this.layer.getChildren().add(this.imageView);
    }

    public void removeFromLayer() {
        this.layer.getChildren().remove(this.imageView);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDx() {
        return dx;
    }

    public void setDx(int dx) {
        this.dx = dx;
    }

    public int getDy() {
        return dy;
    }

    public void setDy(int dy) {
        this.dy = dy;
    }

    public boolean isRemovable() {
        return removable;
    }

    public void move() {
        x += dx;
        y += dy;
    }

    public ImageView getView() {
        return imageView;
    }
    
    protected void changeImage(Image image) {
    	removeFromLayer();
    	this.imageView = new ImageView(image);
    	updateUI();
    	addToLayer();
    }

    public void updateUI() {
        imageView.relocate(x-w/2, y-h/2);
    }

    public double getWidth() {
        return w;
    }

    public double getHeight() {
        return h;
    }

    public void remove() {
        this.removable = true;
    }
}
