package de.lfstudios.game.core.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;

public class Action
{
	private Texture texture;
	private TextureRegion[] textureRegion;
	private int[][] frameMap;

	private Animation downAnimation;
	private Animation upAnimation;
	private Animation leftAnimation;
	private Animation rightAnimation;

	private Animation downLeftAnimation;
	private Animation downRightAnimation;
	private Animation upLeftAnimation;
	private Animation upRightAnimation;

	private boolean isAnimationLooping;
	private int direction;
	private float frameDuration;
	private Music sound;
	private float soundVolume;
	private boolean loopSound;
	private boolean hasSound;

	/**
	 *
	 * @param texturePath
	 * @param x
	 * @param y
	 * @param frameDuration
	 * @param frameMap
	 * @param isAnimationLooping
	 */
	public Action(String texturePath,
				  int x,
				  int y,
				  float frameDuration,
				  int[][] frameMap,
				  boolean isAnimationLooping)
	{
			this(texturePath,
				 x,
				 y,
				 frameDuration,
				 frameMap,
				 isAnimationLooping,
				 null,
				 0,
				 false);
	}

	/**
	 *
	 * @param texturePath
	 * @param x
	 * @param y
	 * @param frameDuration
	 * @param frameMap
	 * @param isAnimationLooping
	 * @param soundPath
	 * @param soundVolume
	 * @param loopSound
	 */
	public Action(String texturePath,
				  int x,
				  int y,
				  float frameDuration,
				  int[][] frameMap,
				  boolean isAnimationLooping,
				  String soundPath,
				  float soundVolume,
				  boolean loopSound)
	{
		this.texture = this.loadTexture(texturePath);
		this.textureRegion = setupRegion(this.texture, x, y);
		this.frameMap = frameMap;
		this.isAnimationLooping = isAnimationLooping;
		this.frameDuration = frameDuration;
		this.sound = this.loadSound(soundPath);
		this.soundVolume = soundVolume;
		this.loopSound = loopSound;

		this.setupAnimations();
		this.setupSound();
	}

	/**
	 *
	 * @param path
	 * @return
	 */
	private Texture loadTexture(String path)
	{
		if(path != null)
		{
			return new Texture(Gdx.files.internal(path));
		}
		return null;
	}

	/**
	 *
	 * @param path
	 * @return
	 */
	private Music loadSound(String path)
	{
		if(path != null)
		{
			this.hasSound = true;
			return Gdx.audio.newMusic((Gdx.files.internal(path)));
		}
		this.hasSound = false;
		return null;
	}

	private void setupAnimations()
	{
		Array<TextureRegion> down = new Array<TextureRegion>();
		Array<TextureRegion> up = new Array<TextureRegion>();
		Array<TextureRegion> left = new Array<TextureRegion>();
		Array<TextureRegion> right = new Array<TextureRegion>();
		Array<TextureRegion> downLeft = new Array<TextureRegion>();
		Array<TextureRegion> downRight = new Array<TextureRegion>();
		Array<TextureRegion> upLeft = new Array<TextureRegion>();
		Array<TextureRegion> upRight = new Array<TextureRegion>();

		for(int i = 0; i < this.frameMap[0].length; i++) down.add(this.textureRegion[this.frameMap[0][i]]);
		for(int i = 0; i < this.frameMap[1].length; i++) up.add(this.textureRegion[this.frameMap[1][i]]);
		for(int i = 0; i < this.frameMap[2].length; i++) left.add(this.textureRegion[this.frameMap[2][i]]);
		for(int i = 0; i < this.frameMap[3].length; i++) right.add(this.textureRegion[this.frameMap[3][i]]);
		for(int i = 0; i < this.frameMap[4].length; i++) downLeft.add(this.textureRegion[this.frameMap[4][i]]);
		for(int i = 0; i < this.frameMap[5].length; i++) downRight.add(this.textureRegion[this.frameMap[5][i]]);
		for(int i = 0; i < this.frameMap[6].length; i++) upLeft.add(this.textureRegion[this.frameMap[6][i]]);
		for(int i = 0; i < this.frameMap[7].length; i++) upRight.add(this.textureRegion[this.frameMap[7][i]]);

		this.downAnimation = new Animation(this.frameDuration, down);
		this.upAnimation = new Animation(this.frameDuration, up);
		this.leftAnimation = new Animation(this.frameDuration, left);
		this.rightAnimation = new Animation(this.frameDuration, right);
		this.downLeftAnimation = new Animation(this.frameDuration, downLeft);
		this.downRightAnimation = new Animation(this.frameDuration, downRight);
		this.upLeftAnimation = new Animation(this.frameDuration, upLeft);
		this.upRightAnimation = new Animation(this.frameDuration, upRight);

		if(this.isAnimationLooping)
		{
			this.downAnimation.setPlayMode(Animation.PlayMode.LOOP);
			this.upAnimation.setPlayMode(Animation.PlayMode.LOOP);
			this.leftAnimation.setPlayMode(Animation.PlayMode.LOOP);
			this.rightAnimation.setPlayMode(Animation.PlayMode.LOOP);
			this.downLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);
			this.downRightAnimation.setPlayMode(Animation.PlayMode.LOOP);
			this.upLeftAnimation.setPlayMode(Animation.PlayMode.LOOP);
			this.upRightAnimation.setPlayMode(Animation.PlayMode.LOOP);
		}
		else
		{
			this.downAnimation.setPlayMode(Animation.PlayMode.NORMAL);
			this.upAnimation.setPlayMode(Animation.PlayMode.NORMAL);
			this.leftAnimation.setPlayMode(Animation.PlayMode.NORMAL);
			this.rightAnimation.setPlayMode(Animation.PlayMode.NORMAL);
			this.downLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);
			this.downRightAnimation.setPlayMode(Animation.PlayMode.NORMAL);
			this.upLeftAnimation.setPlayMode(Animation.PlayMode.NORMAL);
			this.upRightAnimation.setPlayMode(Animation.PlayMode.NORMAL);
		}
	}

	private void setupSound()
	{
		if(this.hasSound)
		{
			this.sound.setVolume(this.soundVolume);
			this.sound.setLooping(this.loopSound);
		}
	}

	/**
	 *
	 * @param texture
	 * @return
	 */
	private TextureRegion[] setupRegion(Texture texture, int x, int y)
	{
		TextureRegion[][] tmp = TextureRegion.split(texture,
													texture.getWidth() / x,
													texture.getHeight() / y);
		TextureRegion[] region = new TextureRegion[x * y];
		int index = 0;
		for (int i = 0; i < y; i++)
		{
			for (int j = 0; j < x; j++)
			{
				region[index++] = tmp[i][j];
			}
		}
		return region;
	}

	public void dispose()
	{
		this.texture.dispose();
		if(this.sound != null) this.sound.dispose();
	}

	/**
	 *
	 * @return
	 */
	public int getDirection()
	{
		return this.direction;
	}

	/**
	 *
	 * @param direction
	 */
	public void setDirection(int direction)
	{
		this.direction = direction;
	}

	/**
	 * 1 = down
	 * 2 = up
	 * 3 = left
	 * 4 = right
	 *
	 * 5 = down left
	 * 6 = down right
	 * 7 = up left
	 * 8 = up right
	 *
	 * @return
	 */
	public Animation getCurrentAnimation()
	{
//		System.out.println("DIR: " + this.getDirection());
		if(this.getDirection() == 1) return this.downAnimation;
		if(this.getDirection() == 2) return this.upAnimation;
		if(this.getDirection() == 3) return this.leftAnimation;
		if(this.getDirection() == 4) return this.rightAnimation;
		if(this.getDirection() == 5) return this.downLeftAnimation;
		if(this.getDirection() == 6) return this.downRightAnimation;
		if(this.getDirection() == 7) return this.upLeftAnimation;
		if(this.getDirection() == 8) return this.upRightAnimation;
		return this.downAnimation;
	}

	/**
	 *
	 * @return
	 */
	public Music getSound()
	{
		return this.sound;
	}

	/**
	 *
	 * @return
	 */
	public boolean hasSound()
	{
		return this.hasSound;
	}

	/**
	 *
	 * @return
	 */
	public boolean isAnimationLooping()
	{
		return this.isAnimationLooping;
	}
}
