package net.kerupani129.sjgl.map.ai;

import org.newdawn.slick.SlickException;

import net.kerupani129.sjgl.SContainer;
import net.kerupani129.sjgl.SGame;
import net.kerupani129.sjgl.map.object.TDirection;
import net.kerupani129.sjgl.map.object.TObjectTile;

public abstract class TAITranslationInTiles extends TAI {

	//
	// フィールド
	//
	private AIBackgroundTranslationInTiles aiBackground;

	//
	// コンストラクタ
	//
	public TAITranslationInTiles(TObjectTile obj) {
		super(obj);

		aiBackground = (AIBackgroundTranslationInTiles) obj.getAI(AIBackgroundTranslationInTiles.class);
		if ( null == aiBackground ) {
			aiBackground = new AIBackgroundTranslationInTiles(obj);
			obj.addAI(-1, aiBackground);
		}

		setAbsoluteSpeed(128, 128);

	}

	//
	// メソッド
	//
	/**
	 * 移動速度絶対値設定
	 */
	protected void setAbsoluteSpeed(float absSpeedX, float absSpeedY) {
		aiBackground.setAbsoluteSpeed(absSpeedX, absSpeedY);
	}

	/**
	 * 移動開始
	 */
	protected void startTranslationInTiles(int x, int y) {
		aiBackground.startTranslationInTiles(x, y);
	}

	// TODO: 当たり判定をしたくない場合はどうするか
	protected void startTranslationInOneTile(TDirection dir) {

		((TObjectTile) obj).setDirection(dir);

		int x = ((TObjectTile) obj).getLookingXInTiles();
		int y = ((TObjectTile) obj).getLookingYInTiles();

		if ( "false".equals(obj.map.getTilePropertyInTiles(x, y, "blocked", "false")) ) {
			int dirX = ((TObjectTile) obj).getDirXInTiles();
			int dirY = ((TObjectTile) obj).getDirYInTiles();
			startTranslationInTiles(dirX, dirY);
		}

	}

	/**
	 * 移動中かどうか
	 */
	protected boolean isTranslating() {
		return aiBackground.isTranslating();
	}

}

class AIBackgroundTranslationInTiles extends TAI {

	//
	// フィールド
	//
	private float translationX = 0, translationY = 0;
	private float translatedX  = 0, translatedY  = 0;
	private float speedX = 0, speedY = 0;
	private float absSpeedX = 0, absSpeedY = 0;
	private boolean xIsTranslated = true, yIsTranslated = true;

	//
	// コンストラクタ
	//
	public AIBackgroundTranslationInTiles(TObjectTile obj) {
		super(obj);
		modifyLocationInTiles();

		translationX = obj.getX();
		translationY = obj.getY();

		translatedX = translationX;
		translatedY = translationY;
	}

	//
	// メソッド
	//
	/**
	 * 移動速度絶対値設定
	 */
	public void setAbsoluteSpeed(float absSpeedX, float absSpeedY) {
		if ( absSpeedX < 0 ) {
			this.absSpeedX = -absSpeedX;
		} else {
			this.absSpeedX = absSpeedX;
		}
		if ( absSpeedY < 0 ) {
			this.absSpeedY = -absSpeedY;
		} else {
			this.absSpeedY = absSpeedY;
		}
	}

	/**
	 * 位置をタイル単位に修正
	 */
	private void modifyLocationInTiles() {
		int xInTiles = (int) ((obj.getX() + obj.map.getTileWidth()  / 2) / obj.map.getTileWidth() );
		int yInTiles = (int) ((obj.getY() + obj.map.getTileHeight() / 2) / obj.map.getTileHeight());

		translatedX = xInTiles * obj.map.getTileWidth();
		translatedY = yInTiles * obj.map.getTileHeight();

		obj.setLocation(translatedX, translatedY);
	}

	/**
	 * 移動開始
	 */
	public void startTranslationInTiles(int x, int y) {
		if ( x == 0 && y == 0 ) return;

		xIsTranslated = false;
		yIsTranslated = false;

		// 移動相対距離と符号含む速度を求める
		translationX = obj.map.getTileWidth()  * x;
		translationY = obj.map.getTileHeight() * y;

		// speedX = -Math.abs(speedX);
		// だと float の誤差が心配？
		if ( translationX < 0 ) {
			this.speedX = -absSpeedX;
		} else {
			this.speedX = absSpeedX;
		}
		if ( translationY < 0 ) {
			this.speedY = -absSpeedY;
		} else {
			this.speedY = absSpeedY;
		}

		// 現在の絶対値を求め、移動絶対位置を求める
		translatedX = obj.getX();
		translatedY = obj.getY();

		translationX += translatedX;
		translationY += translatedY;
	}

	/**
	 * 移動中かどうか
	 */
	public boolean isTranslating() {
		return !xIsTranslated || !yIsTranslated;
	}

	/**
	 * 移動
	 */
	@Override
	public void update(SContainer container, SGame game, int delta) throws SlickException {

		// X 方向
		if ( (translationX - translatedX) / speedX > 0 ) { // 移動中
			translatedX += speedX * delta / 1000;
			if ( (translationX - translatedX) / speedX <= 0 ) {
				translatedX = translationX;
			}
		} else { // 移動が完了した
			translatedX = translationX;
			xIsTranslated = true;
		}

		// Y 方向
		if ( (translationY - translatedY) / speedY > 0 ) { // 移動中
			translatedY += speedY * delta / 1000;
			if ( (translationY - translatedY) / speedY <= 0 ) {
				translatedY = translationY;
			}
		} else { // 移動が完了した
			translatedY = translationY;
			yIsTranslated = true;
		}

		// obj に反映
		if ( isTranslating() ) { // 移動中
			obj.setLocation(translatedX, translatedY);
			((TObjectTile) obj).setTranslating(true);
		} else {                 // 移動が完了した
			modifyLocationInTiles();
			((TObjectTile) obj).setTranslating(false);
		}

	}

}
