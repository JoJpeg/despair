package de.lfstudios.game.core.player;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;

import java.util.ArrayList;

/**
 * @author vwiebe
 */
public class Player
{
	private BodyDef bodyDef;
	private Body body;

	private TextureRegion currentFrame;
	private ArrayList<Action> actionList;
	private Action standAction;
	private Action walkAction;
	private Action runAction;
	private Action attackAction;
	private Action blockAction;
	private Action currentAction;
	private boolean isBlockReleased;

	private float stateTime;
	private float posX;
	private float posY;
	private float speed;

	public Player()
	{

		this.bodyDef = new BodyDef();
		this.bodyDef.type = BodyDef.BodyType.DynamicBody;

		this.standAction = new Action("game/player_run_0.png",
									  8,
									  4,
									  0.5f,
									  new int[][]
										  {
											  {1,3},
											  {5,7},
											  {13,15},
											  {9,11},
											  {21,23},
											  {17,19},
											  {25,27},
											  {29,31}
										  },
									  true);

		this.walkAction = new Action("game/player_run_0.png",
									 8,
									 4,
									 0.25f,
									 new int[][]
										 {
											 {0,1,2,3},
											 {4,5,6,7},
											 {12,13,14,15},
											 {8,9,10,11},
											 {20,21,22,23},
											 {16,17,18,19},
											 {24,25,26,27},
											 {28,29,30,31}
										 },
									 true,
									 "sounds/walk.mp3",
									 0.05f,
									 true);

		this.runAction = new Action("game/player_run_0.png",
									8,
									4,
									0.15f,
									new int[][]
										{
											{0,1,2,3},
											{4,5,6,7},
											{12,13,14,15},
											{8,9,10,11},
											{20,21,22,23},
											{16,17,18,19},
											{24,25,26,27},
											{28,29,30,31}
										},
									true,
									"sounds/run.mp3",
									0.05f,
									true);

		this.attackAction = new Action("game/player_hit_0.png",
									   8,
									   4,
									   0.075f,
									   new int[][]
										   {
											   {0,1,2},
											   {4,5,6},
											   {12,13,14},
											   {8,9,10},
											   {20,21,22},
											   {16,17,18},
											   {24,25,26},
											   {28,29,30}
										   },
									   false,
									   "sounds/swing_1.mp3",
									   0.2f,
									   false);

		this.blockAction = new Action("game/player_def_0.png",
									  8,
									  4,
									  0.075f,
									  new int[][]
										  {
											  {0,1,2},
											  {4,5,6},
											  {12,13,14},
											  {8,9,10},
											  {20,21,22},
											  {16,17,18},
											  {24,25,26},
											  {28,29,30}
										  },
									  false,
									  "sounds/block_0.mp3",
									  0.2f,
									  false);

		this.actionList = new ArrayList<Action>();

		this.actionList.add(this.standAction);
		this.actionList.add(this.walkAction);
		this.actionList.add(this.runAction);
		this.actionList.add(this.attackAction);
		this.actionList.add(this.blockAction);

		// default
		this.currentAction = this.standAction;
		this.speed = 2.0f;
		this.setBlockReleased(true);
		this.setStateTime(0.0f);
	}

	/**
	 *
	 * @param x
	 * @param y
	 */
	private void setDirection(float x, float y)
	{
		Vector2 v = new Vector2(x, y);
		float angle = v.angle(new Vector2(0, 1));

		if( angle == 0)
		{
			this.setCurrentAction(this.standAction); // standing
		}
		else if(angle >=  157.5 || angle <= -157.5)
		{
			this.updateActionDirections(1); // facing down
		}
		else if(angle > 112.5 && angle < 157.5)
		{
			this.updateActionDirections(6); // facing down right
		}
		else if(angle > -157.5  && angle < -112.5)
		{
			this.updateActionDirections(5); // facing down left
		}
		else if(angle > 67.5 && angle < 112.5)
		{
			this.updateActionDirections(4); // facing right
		}
		else if(angle < -67.5 && angle > -112.5)
		{
			this.updateActionDirections(3); // facing left
		}
		else if(angle < -22.5 && angle > -67.5)
		{
			this.updateActionDirections(7); // facing up left
		}
		else if(angle > 22.5 && angle < 67.5)
		{
			this.updateActionDirections(8); // facing up right
		}
		else if(angle < 22.5 || angle > -22.5)
		{
			this.updateActionDirections(2); // facing up
		}

		if(v.len() > 0.5f && angle != 0)
		{
			this.setCurrentAction(this.runAction);
		}
		else if(v.len() <= 0.5f && angle != 0)
		{
			this.setCurrentAction(this.walkAction);
		}
	}

	private void updateActionDirections(int direction)
	{
		for(Action action : this.actionList)
		{
			action.setDirection(direction);
		}
	}

	public void attack() { this.setCurrentAction(this.attackAction); }

	public void block()
	{
		this.setBlockReleased(false);
		this.setCurrentAction(this.blockAction);
	}

	public void stand() { this.setCurrentAction(this.standAction); }
	public void walk() { this.setCurrentAction(this.walkAction); }
	public void run() { this.setCurrentAction(this.attackAction); }

	/**
	 *
	 * @param touchpad
	 */
	public void update(Touchpad touchpad)
	{
		if(this.getCurrentAction().equals(this.blockAction))
		{
			if(!this.isBlockReleased())
			{
				this.getBody().setLinearVelocity(0, 0);
			}
			else if(this.getCurrentAction().getCurrentAnimation().isAnimationFinished(this.getStateTime()))
			{
				this.setCurrentAction(this.standAction);
			}
		}
		else
		{
			if(this.getCurrentAction().equals(this.attackAction))
			{
				this.speed = 1.0f;

				if(this.getCurrentAction()
					   .getCurrentAnimation()
					   .isAnimationFinished(this.getStateTime()))
				{
					this.setCurrentAction(standAction);
				}
			}
			else
			{
				this.speed = 2.0f;

				this.setDirection(touchpad.getKnobPercentX(),
								  touchpad.getKnobPercentY());
			}

			if(Gdx.input.isTouched())
			{
				this.getBody().setLinearVelocity(touchpad.getKnobPercentX() * this.speed,
												 touchpad.getKnobPercentY() * this.speed);
			}
			else
			{
				this.getBody().setLinearVelocity(0, 0);
			}
		}

		// prepare current player frame
		this.setStateTime(this.getStateTime() + Gdx.graphics.getDeltaTime());
		this.setCurrentFrame(this.getCurrentAction()
								 .getCurrentAnimation()
								 .getKeyFrame(this.getStateTime(),
											  this.getCurrentAction()
												  .isAnimationLooping()));
	}

	public void dispose()
	{
		this.standAction.dispose();
		this.walkAction.dispose();
		this.runAction.dispose();
		this.attackAction.dispose();
		this.blockAction.dispose();
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
	public Action getCurrentAction()
	{
		return this.currentAction;
	}

	/**
	 *
	 * @return
	 */
	public BodyDef getBodyDef()
	{
		return this.bodyDef;
	}

	/**
	 *
	 * @return
	 */
	public Body getBody()
	{
		return this.body;
	}

	/**
	 *
	 * @param body
	 */
	public void setBody(Body body)
	{
		this.body = body;
	}

	/**
	 *
	 * @param currentAction
	 */
	public void setCurrentAction(Action currentAction)
	{
		if(getCurrentAction().equals(currentAction)) return;

		if(this.getCurrentAction().hasSound() && this.getCurrentAction().getSound().isPlaying())
		{
			this.getCurrentAction().getSound().stop();
		}

		this.setStateTime(0.0f);
		this.currentAction = currentAction;

		if(this.getCurrentAction().hasSound() && !this.getCurrentAction().getSound().isPlaying())
		{
			this.getCurrentAction().getSound().play();
		}
	}

	/**
	 *
	 * @return
	 */
	public boolean isBlockReleased()
	{
		return this.isBlockReleased;
	}

	/**
	 *
	 * @param isBlockReleased
	 */
	public void setBlockReleased(boolean isBlockReleased)
	{
		this.isBlockReleased = isBlockReleased;
	}
}
