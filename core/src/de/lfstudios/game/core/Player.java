package de.lfstudios.game.core;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

public class Player
{
	private Texture moveTexture;
	private Texture attackTexture;
	private Texture blockTexture;
	private TextureRegion[] moveRegions;
	private TextureRegion[] attackRegions;
	private TextureRegion[] blockRegions;
	private TextureRegion currentFrame;
	private Animation currentAnimation;
	private Animation lastAnimation;
	private boolean isDoingAction;

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

	private Animation hitDownAnimation;
	private Animation hitUpAnimation;
	private Animation hitLeftAnimation;
	private Animation hitRightAnimation;

	private Animation blockDownAnimation;
	private Animation blockUpAnimation;
	private Animation blockLeftAnimation;
	private Animation blockRightAnimation;

	private Music walkSound;
	private Music runSound;
	private Music swingSound;
	private Music blockSound;

	public Player()
	{
		this.setMoveTexture(new Texture(Gdx.files.internal("game/player_run.png")));
		this.setAttackTexture(new Texture(Gdx.files.internal("game/player_hit.png")));
		this.setBlockTexture(new Texture(Gdx.files.internal("game/player_def.png")));
		this.setupAnimation();
		this.setupSound();
	}

	private void setupSound()
	{
		this.runSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/run.mp3"));
		this.walkSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/walk.mp3"));
		this.swingSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/swing_1.mp3"));
		this.blockSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/block_0.mp3"));
		this.runSound.setVolume(0.05f);
		this.walkSound.setVolume(0.05f);
		this.swingSound.setVolume(0.5f);
		this.blockSound.setVolume(0.5f);
		this.runSound.setLooping(true);
		this.walkSound.setLooping(true);
		this.swingSound.setLooping(false);
		this.blockSound.setLooping(false);

	}

	private TextureRegion[] setupRegion(Texture texture)
	{
		TextureRegion[][] tmp = TextureRegion.split(texture,
													texture.getWidth() / 8,
													texture.getHeight() / 8);
		TextureRegion[] region = new TextureRegion[8 * 8];
		int index = 0;
		for (int i = 0; i < 8; i++)
		{
			for (int j = 0; j < 8; j++)
			{
				region[index++] = tmp[i][j];
			}
		}

		return region;
	}

	private void setupAnimation()
	{
		this.moveRegions = this.setupRegion(this.getMoveTexture());
		this.attackRegions = this.setupRegion(this.getAttackTexture());
		this.blockRegions = this.setupRegion(this.getBlockTexture());

		this.runDownAnimation = new Animation(0.125f, this.moveRegions[0], this.moveRegions[1], this.moveRegions[2], this.moveRegions[3]);
		this.runUpAnimation = new Animation(0.125f, this.moveRegions[4], this.moveRegions[5], this.moveRegions[6], this.moveRegions[7]);
		this.runRightAnimation = new Animation(0.125f, this.moveRegions[8], this.moveRegions[9], this.moveRegions[10], this.moveRegions[11]);
		this.runLeftAnimation = new Animation(0.125f, this.moveRegions[12], this.moveRegions[13], this.moveRegions[14], this.moveRegions[15]);

		this.walkDownAnimation = new Animation(0.250f, this.moveRegions[0], this.moveRegions[1], this.moveRegions[2], this.moveRegions[3]);
		this.walkUpAnimation = new Animation(0.250f, this.moveRegions[4], this.moveRegions[5], this.moveRegions[6], this.moveRegions[7]);
		this.walkRightAnimation = new Animation(0.250f, this.moveRegions[8], this.moveRegions[9], this.moveRegions[10], this.moveRegions[11]);
		this.walkLeftAnimation = new Animation(0.250f, this.moveRegions[12], this.moveRegions[13], this.moveRegions[14], this.moveRegions[15]);

		this.standDownAnimation = new Animation(0.5f, this.moveRegions[1], this.moveRegions[3]);
		this.standUpAnimation = new Animation(0.5f, this.moveRegions[5], this.moveRegions[7]);
		this.standRightAnimation = new Animation(0.5f, this.moveRegions[9], this.moveRegions[11]);
		this.standLeftAnimation = new Animation(0.5f, this.moveRegions[13], this.moveRegions[15]);

		this.hitDownAnimation = new Animation(0.125f, this.attackRegions[0], this.attackRegions[1], this.attackRegions[2], this.moveRegions[1]);
		this.hitUpAnimation = new Animation(0.125f, this.attackRegions[4], this.attackRegions[5], this.attackRegions[6], this.moveRegions[5]);
		this.hitLeftAnimation = new Animation(0.125f, this.attackRegions[12], this.attackRegions[13], this.attackRegions[14], this.moveRegions[13]);
		this.hitRightAnimation = new Animation(0.125f, this.attackRegions[8], this.attackRegions[9], this.attackRegions[10], this.moveRegions[9]);

		this.blockDownAnimation = new Animation(0.125f, this.blockRegions[0], this.blockRegions[1], this.blockRegions[2], this.blockRegions[1]);
		this.blockUpAnimation = new Animation(0.125f, this.blockRegions[4], this.blockRegions[5], this.blockRegions[6], this.blockRegions[5]);
		this.blockLeftAnimation = new Animation(0.125f, this.blockRegions[12], this.blockRegions[13], this.blockRegions[14], this.blockRegions[13]);
		this.blockRightAnimation = new Animation(0.125f, this.blockRegions[8], this.blockRegions[9], this.blockRegions[10], this.blockRegions[9]);

		this.setCurrentAnimation(this.runDownAnimation);
		this.setStateTime(0.0f);
	}

	/**
	 *
	 * @param x
	 * @param y
	 */
	public void setAnimationType(float x, float y)
	{
		if(!this.isDoingAction)
		{
			this.setMovementAnimationType(x, y);
		}
	}

	/**
	 * 1 = hit
	 * 2 = block
	 * @param type
	 */
	public void setActionAnimationType(int type)
	{
		if(!this.isDoingAction())
		{
			this.setStateTime(0.0f);
			this.isDoingAction = true;

			if(this.getCurrentAnimation().equals(this.standDownAnimation) ||
			   this.getCurrentAnimation().equals(this.walkDownAnimation) ||
			   this.getCurrentAnimation().equals(this.runDownAnimation))
			{
				if(type == 1) this.setCurrentAnimation(this.hitDownAnimation);
				if(type == 2) this.setCurrentAnimation(this.blockDownAnimation);
			}
			else if(this.getCurrentAnimation().equals(this.standUpAnimation) ||
					this.getCurrentAnimation().equals(this.walkUpAnimation) ||
					this.getCurrentAnimation().equals(this.runUpAnimation))
			{
				if(type == 1) this.setCurrentAnimation(this.hitUpAnimation);
				if(type == 2) this.setCurrentAnimation(this.blockUpAnimation);
			}
			else if(this.getCurrentAnimation().equals(this.standLeftAnimation) ||
					this.getCurrentAnimation().equals(this.walkLeftAnimation) ||
					this.getCurrentAnimation().equals(this.runLeftAnimation))
			{
				if(type == 1) this.setCurrentAnimation(this.hitLeftAnimation);
				if(type == 2) this.setCurrentAnimation(this.blockLeftAnimation);
			}
			else if(this.getCurrentAnimation().equals(this.standRightAnimation) ||
					this.getCurrentAnimation().equals(this.walkRightAnimation) ||
					this.getCurrentAnimation().equals(this.runRightAnimation))
			{
				if(type == 1) this.setCurrentAnimation(this.hitRightAnimation);
				if(type == 2) this.setCurrentAnimation(this.blockRightAnimation);
			}
		}
	}

	/**
	 *
	 * @param x
	 * @param y
	 */
	private void setMovementAnimationType(float x, float y)
	{
		Vector2 v = new Vector2(x, y);
		float angle = v.angle(new Vector2(0,1));

		if(getLastAnimation() == null)
		{
			this.setLastAnimation(this.runDownAnimation);
		}

		if( angle == 0)
		{
			if(this.getLastAnimation().equals(this.runDownAnimation) ||
			   this.getLastAnimation().equals(this.walkDownAnimation) ||
			   this.getCurrentAnimation().equals(this.hitDownAnimation) ||
			   this.getCurrentAnimation().equals(this.blockDownAnimation))
			{
				this.idle(this.standDownAnimation);
			}

			if(this.getLastAnimation().equals(this.runUpAnimation) ||
			   this.getLastAnimation().equals(this.walkUpAnimation) ||
			   this.getCurrentAnimation().equals(this.hitUpAnimation) ||
			   this.getCurrentAnimation().equals(this.blockUpAnimation))
			{
				this.idle(this.standUpAnimation);
			}

			if(this.getLastAnimation().equals(this.runRightAnimation) ||
			   this.getLastAnimation().equals(this.walkRightAnimation)||
			   this.getCurrentAnimation().equals(this.hitRightAnimation) ||
			   this.getCurrentAnimation().equals(this.blockRightAnimation))
			{
				this.idle(this.standRightAnimation);
			}

			if(this.getLastAnimation().equals(this.runLeftAnimation) ||
			   this.getLastAnimation().equals(this.walkLeftAnimation) ||
			   this.getCurrentAnimation().equals(this.hitLeftAnimation) ||
			   this.getCurrentAnimation().equals(this.blockLeftAnimation))
			{
				this.idle(this.standLeftAnimation);
			}
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

	public void attack()
	{
		System.out.println("ATTACK!");
		this.setActionAnimationType(1);
		if(!this.swingSound.isPlaying() && !this.blockSound.isPlaying()) this.swingSound.play();
	}

	public void block()
	{
		System.out.println("BLOCK!");
		this.setActionAnimationType(2);
		if(!this.blockSound.isPlaying() && !this.swingSound.isPlaying()) this.blockSound.play();
	}

	/**
	 *
	 * @param touchpad
	 */
	public void update(Touchpad touchpad)
	{

		if(this.isDoingAction &&
		   (this.getCurrentAnimation().equals(this.hitDownAnimation) ||
			this.getCurrentAnimation().equals(this.hitUpAnimation) ||
			this.getCurrentAnimation().equals(this.hitLeftAnimation) ||
			this.getCurrentAnimation().equals(this.hitRightAnimation)) ||
		    this.getCurrentAnimation().equals(this.blockDownAnimation) ||
		    this.getCurrentAnimation().equals(this.blockUpAnimation) ||
		    this.getCurrentAnimation().equals(this.blockLeftAnimation) ||
		    this.getCurrentAnimation().equals(this.blockRightAnimation))
		{
			if(this.getCurrentAnimation().isAnimationFinished(this.getStateTime()))
			{
				this.isDoingAction = false;
				this.setAnimationType(touchpad.getKnobPercentX(),
									  touchpad.getKnobPercentY());
			}
		}

		if(Gdx.input.isTouched() && !this.isDoingAction())
		{
			this.setPosX(this.getPosX() + touchpad.getKnobPercentX() * 4);
			this.setPosY(this.getPosY() + touchpad.getKnobPercentY() * 4);
		}

		if(!this.isDoingAction())
		{
			this.setAnimationType(touchpad.getKnobPercentX(),
								  touchpad.getKnobPercentY());
		}


		// prepare current player frame
		this.setStateTime(this.getStateTime() + Gdx.graphics.getDeltaTime());
		this.setCurrentFrame(this.getCurrentAnimation()
								 .getKeyFrame(this.getStateTime(),
											  true));

	}

	public void dispose()
	{
		this.getMoveTexture().dispose();
		this.walkSound.dispose();
		this.runSound.dispose();
		this.swingSound.dispose();
		this.blockSound.dispose();
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
	public Texture getMoveTexture()
	{
		return this.moveTexture;
	}

	/**
	 *
	 * @param moveTexture
	 */
	public void setMoveTexture(Texture moveTexture)
	{
		this.moveTexture = moveTexture;
	}

	/**
	 *
	 * @return
	 */
	public Texture getAttackTexture()
	{
		return this.attackTexture;
	}

	/**
	 *
	 * @param attackTexture
	 */
	public void setAttackTexture(Texture attackTexture)
	{
		this.attackTexture = attackTexture;
	}

	/**
	 *
	 * @return
	 */
	public Texture getBlockTexture()
	{
		return this.blockTexture;
	}

	/**
	 *
	 * @param blockTexture
	 */
	public void setBlockTexture(Texture blockTexture)
	{
		this.blockTexture = blockTexture;
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

	/**
	 *
	 * @return
	 */
	public boolean isDoingAction()
	{
		return isDoingAction;
	}
}
