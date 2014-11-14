package de.lfstudios.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

public class Player
{
	private Texture texture;
	private TextureRegion[] regions;
	private TextureRegion currentFrame;
	private Animation currentAnimation;
	private Animation lastAnimation;
	private float stateTime;
	private float posX;
	private float posY;

	private Animation standDownAnimation;
	private Animation standUpAnimation;
	private Animation standLeftAnimation;
	private Animation standRightAnimation;

	private Animation runDownAnimation;
	private Animation runUpAnimation;
	private Animation runLeftAnimation;
	private Animation runRightAnimation;

	private Animation walkDownAnimation;
	private Animation walkUpAnimation;
	private Animation walkLeftAnimation;
	private Animation walkRightAnimation;

	private Music walkSound;
	private Music runSound;

	public Player()
	{
		this.setTexture(new Texture(Gdx.files.internal("game/player_run.png")));
		this.setupAnimation();
		this.setupSound();
	}

	private void setupSound()
	{
		this.runSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/run.mp3"));
		this.walkSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/walk.mp3"));
		this.runSound.setVolume(0.05f);
		this.walkSound.setVolume(0.05f);
		this.runSound.setLooping(true);
		this.walkSound.setLooping(true);
	}

	private void setupAnimation()
	{
		TextureRegion[][] tmp = TextureRegion.split(this.getTexture(),
													this.getTexture().getWidth() / 8,
													this.getTexture().getHeight() / 8);
		this.regions = new TextureRegion[8 * 8];
		int index = 0;
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				this.regions[index++] = tmp[i][j];
			}
		}

		this.runDownAnimation = new Animation(0.125f, this.regions[0], this.regions[1], this.regions[2], this.regions[3]);
		this.runUpAnimation = new Animation(0.125f, this.regions[4], this.regions[5], this.regions[6], this.regions[7]);
		this.runRightAnimation = new Animation(0.125f, this.regions[8], this.regions[9], this.regions[10], this.regions[11]);
		this.runLeftAnimation = new Animation(0.125f, this.regions[12], this.regions[13], this.regions[14], this.regions[15]);

		this.walkDownAnimation = new Animation(0.250f, this.regions[0], this.regions[1], this.regions[2], this.regions[3]);
		this.walkUpAnimation = new Animation(0.250f, this.regions[4], this.regions[5], this.regions[6], this.regions[7]);
		this.walkRightAnimation = new Animation(0.250f, this.regions[8], this.regions[9], this.regions[10], this.regions[11]);
		this.walkLeftAnimation = new Animation(0.250f, this.regions[12], this.regions[13], this.regions[14], this.regions[15]);

		this.standDownAnimation = new Animation(0.5f, this.regions[1], this.regions[3]);
		this.standUpAnimation = new Animation(0.5f, this.regions[5], this.regions[7]);
		this.standRightAnimation = new Animation(0.5f, this.regions[9], this.regions[11]);
		this.standLeftAnimation = new Animation(0.5f, this.regions[13], this.regions[15]);

		this.setCurrentAnimation(this.runDownAnimation);
		this.setStateTime(0f);
	}

	/**
	 *
	 * @param x
	 * @param y
	 */
	public void setAnimationType(float x, float y)
	{
		Vector2 v = new Vector2(x, y);
		float angle = v.angle(new Vector2(0,1));


		if(getLastAnimation() == null) this.setLastAnimation(this.runDownAnimation);

		if( angle == 0)
		{
			if(this.getLastAnimation().equals(this.runDownAnimation) || this.getLastAnimation().equals(this.walkDownAnimation)) this.idle(standDownAnimation);
			if(this.getLastAnimation().equals(this.runUpAnimation) || this.getLastAnimation().equals(this.walkUpAnimation)) this.idle(standUpAnimation);
			if(this.getLastAnimation().equals(this.runRightAnimation) || this.getLastAnimation().equals(this.walkRightAnimation)) this.idle(standRightAnimation);
			if(this.getLastAnimation().equals(this.runLeftAnimation) || this.getLastAnimation().equals(this.walkLeftAnimation)) this.idle(standLeftAnimation);
		}
		// forward
		else if(angle >= 135 || angle <= -135)
		{
			if(v.len() > 0.5f)
			{
				this.run(this.runDownAnimation);
			}
			else
			{
				this.walk(this.walkDownAnimation);
			}
		}
		// right
		else if(angle > 45 && angle < 135)
		{
			if(v.len() > 0.5f)
			{
				this.run(this.runRightAnimation);
			}
			else
			{
				this.walk(this.walkRightAnimation);
			}
		}
		// left
		else if(angle < -45 && angle > -135)
		{
			if(v.len() > 0.5f)
			{
				this.run(this.runLeftAnimation);
			}
			else
			{
				this.walk(this.walkLeftAnimation);
			}
		}
		else if(angle < 45 || angle > -45)
		{
		// up
			if(v.len() > 0.5f)
			{
				this.run(this.runUpAnimation);
			}
			else
			{
				this.walk(this.walkUpAnimation);
			}
		}
	}

	/**
	 *
	 * @param animation
	 */
	private void run(Animation animation)
	{
		if(this.walkSound.isPlaying()) this.walkSound.stop();
		if(!this.runSound.isPlaying()) this.runSound.play();
		this.setCurrentAnimation(animation);
	}

	/**
	 *
	 * @param animation
	 */
	private void walk(Animation animation)
	{
		if(this.runSound.isPlaying()) this.runSound.stop();
		if(!this.walkSound.isPlaying()) this.walkSound.play();
		this.setCurrentAnimation(animation);
	}

	/**
	 *
	 * @param animation
	 */
	private void idle(Animation animation)
	{
		if(this.runSound.isPlaying()) this.runSound.stop();
		if(this.walkSound.isPlaying()) this.walkSound.stop();
		this.setCurrentAnimation(animation);
	}

	public void dispose()
	{
		this.getTexture().dispose();
	}

	/**
	 *
	 * @param x
	 * @param y
	 */
	public void setPosition(float x, float y)
	{
		this.setPosX(x);
		this.setPosY(y);
	}

	/**
	 *
	 * @return
	 */
	public Texture getTexture()
	{
		return this.texture;
	}

	/**
	 *
	 * @param texture
	 */
	public void setTexture(Texture texture)
	{
		this.texture = texture;
	}

	/**
	 *
	 * @return
	 */
	public Animation getCurrentAnimation()
	{
		return this.currentAnimation;
	}

	/**
	 *
	 * @param currentAnimation
	 */
	public void setCurrentAnimation(Animation currentAnimation)
	{
		this.setLastAnimation(this.getCurrentAnimation());
		this.currentAnimation = currentAnimation;

	}

	/**
	 *
	 * @return
	 */
	public Animation getLastAnimation()
	{
		return this.lastAnimation;
	}

	/**
	 *
	 * @param lastAnimation
	 */
	public void setLastAnimation(Animation lastAnimation)
	{
		this.lastAnimation = lastAnimation;
	}

	/**
	 *
	 * @return
	 */
	public float getStateTime()
	{
		return this.stateTime;
	}

	/**
	 *
	 * @param stateTime
	 */
	public void setStateTime(float stateTime)
	{
		this.stateTime = stateTime;
	}

	/**
	 *
	 * @return
	 */
	public TextureRegion getCurrentFrame()
	{
		return this.currentFrame;
	}

	/**
	 *
	 * @param currentFrame
	 */
	public void setCurrentFrame(TextureRegion currentFrame)
	{
		this.currentFrame = currentFrame;
	}

	/**
	 *
	 * @return
	 */
	public float getPosX()
	{
		return this.posX;
	}

	/**
	 *
	 * @param posX
	 */
	public void setPosX(float posX)
	{
		this.posX = posX;
	}

	/**
	 *
	 * @return
	 */
	public float getPosY()
	{
		return this.posY;
	}

	/**
	 *
	 * @param posY
	 */
	public void setPosY(float posY)
	{
		this.posY = posY;
	}
}
