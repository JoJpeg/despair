package de.lfstudios.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.*;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.lfstudios.game.Despair;
import de.lfstudios.game.core.Player;

public class GameScreen implements Screen
{

	private Despair game;
	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	private Skin touchpadSkin;
	private Touchpad.TouchpadStyle touchpadStyle;
	private Drawable touchKnob;
	private Touchpad touchpad;
	private Stage stage;
	private Music backgroundMusic;
	private Music backButtonSound;
	private TiledMap map;
	private TiledMapTileSet tileSet;
	private OrthogonalTiledMapRenderer mapRenderer;
	private Player player;

	private ImageButton attackButton;
	private Skin attackButtonSkin;
	private ImageButton.ImageButtonStyle attackButtonStyle;
	private Drawable attackButtonDrawable;

	private ImageButton blockButton;
	private Skin blockButtonSkin;
	private ImageButton.ImageButtonStyle blockButtonStyle;
	private Drawable blockButtonDrawable;

	public GameScreen(Despair game)
	{
		this.game = game;
		this.camera = new OrthographicCamera();
		this.camera.position.set(0, 0, 0);
		this.camera.update();

		this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/ingame.mp3"));
		this.backButtonSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/gong.mp3"));
		this.backgroundMusic.play();

		this.spriteBatch = new SpriteBatch();
		Gdx.input.setCatchBackKey(true);

		this.touchpadSkin = new Skin();
		this.touchpadSkin.add("knob", new Texture(Gdx.files.internal("game/knob.png")));
		this.touchpadStyle = new Touchpad.TouchpadStyle();
		this.touchKnob = this.touchpadSkin.getDrawable("knob");
		this.touchKnob.setMinWidth(140);
		this.touchKnob.setMinHeight(140);
		this.touchpadStyle.knob = this.touchKnob;
		this.touchpad = new Touchpad(10, this.touchpadStyle);
		this.touchpad.setBounds(40, Gdx.graphics.getHeight() - 290, 350, 350);

		this.stage = new Stage(new ScreenViewport(this.camera), this.spriteBatch);

		this.setupMap();

		this.player = new Player();
		this.player.setPosition(192 * 5, 416 * 5);

		this.attackButtonSkin = new Skin();
		this.attackButtonSkin.add("icon", new Texture(Gdx.files.internal("game/button_atk.png")));
		this.attackButtonStyle = new ImageButton.ImageButtonStyle();
		this.attackButtonDrawable = this.attackButtonSkin.getDrawable("icon");
		this.attackButtonDrawable.setMinWidth(70);
		this.attackButtonDrawable.setMinHeight(70);
		this.attackButtonStyle.imageUp = this.attackButtonDrawable;
		this.attackButton = new ImageButton(this.attackButtonStyle);
		this.attackButton.setBounds(0, 0, 120, 120);

		this.blockButtonSkin = new Skin();
		this.blockButtonSkin.add("icon", new Texture(Gdx.files.internal("game/button_def.png")));
		this.blockButtonStyle = new ImageButton.ImageButtonStyle();
		this.blockButtonDrawable = this.blockButtonSkin.getDrawable("icon");
		this.blockButtonDrawable.setMinWidth(70);
		this.blockButtonDrawable.setMinHeight(70);
		this.blockButtonStyle.imageUp = this.blockButtonDrawable;
		this.blockButton = new ImageButton(this.blockButtonStyle);
		this.blockButton.setBounds(0, 0, 120, 120);

		this.stage.addActor(this.touchpad);
		this.stage.addActor(this.attackButton);
		this.stage.addActor(this.blockButton);
		Gdx.input.setInputProcessor(this.stage);
	}

	private void setupMap()
	{
		this.map = new TmxMapLoader().load("game/map/scene1.tmx");
		this.tileSet =  this.map.getTileSets().getTileSet("ground");
		this.mapRenderer = new OrthogonalTiledMapRenderer(this.map, 5);
	}

	@Override
	public void render(float delta)
	{
		this.clearScreen();
		this.updateMap();
		this.updatePlayer();
		this.updateTouchpad();
		this.updateButtons();
		this.updateCamera();

		//draw
		this.spriteBatch.begin();
		this.spriteBatch.draw(this.player.getCurrentFrame(),
							  this.player.getPosX(),
							  this.player.getPosY(),
							  this.player.getCurrentFrame().getRegionWidth() * 4,
							  this.player.getCurrentFrame().getRegionHeight() * 4);
		this.spriteBatch.end();

		this.stage.draw();


		if (Gdx.input.isKeyPressed(Input.Keys.BACK)){
			this.exit();
		}
	}

	private void updateMap()
	{
		this.mapRenderer.setView(this.camera);
		this.mapRenderer.render();

	}

	private void updateCamera()
	{
		this.camera
			.position.set(this.player.getPosX() + (this.player.getCurrentFrame().getRegionWidth() / 2),
						  this.player.getPosY() + (this.player.getCurrentFrame().getRegionHeight() / 2),
						  0);
		this.camera.update();
	}

	private void updateButtons()
	{
		this.attackButton.setBounds(this.camera.position.x + (Gdx.graphics.getWidth() / 2 - this.attackButton.getWidth() * 1.5f),
									this.camera.position.y - Gdx.graphics.getHeight() / 2 + this.attackButton.getHeight() / 2 + this.attackButton.getHeight() / 2,
									this.attackButton.getWidth(),
									this.attackButton.getHeight());

		this.blockButton.setBounds(this.camera.position.x + (Gdx.graphics.getWidth() / 2 - this.blockButton.getWidth() * 2.5f),
								   this.camera.position.y - Gdx.graphics.getHeight() / 2 + this.attackButton.getHeight() / 2,
								   this.blockButton.getWidth(),
								   this.blockButton.getHeight());
	}

	private void updateTouchpad()
	{
		this.touchpad.setBounds(this.camera.position.x - (Gdx.graphics.getWidth() / 2),
								this.camera.position.y - ((Gdx.graphics.getHeight()) - this.touchpad.getHeight()),
								this.touchpad.getWidth(),
								this.touchpad.getHeight());
	}

	private void updatePlayer()
	{
		if(Gdx.input.isTouched())
		{
			this.player.setPosX(this.player.getPosX() + this.touchpad.getKnobPercentX() * 4);
			this.player.setPosY(this.player.getPosY() + this.touchpad.getKnobPercentY() * 4);
		}

		this.player.setAnimationType(this.touchpad.getKnobPercentX(),
									 this.touchpad.getKnobPercentY());

		// prepare current player frame
		this.player.setStateTime(this.player.getStateTime() + Gdx.graphics.getDeltaTime());
		this.player.setCurrentFrame(this.player.getCurrentAnimation()
											   .getKeyFrame(this.player.getStateTime(),
															true));
	}

	private void clearScreen()
	{
		Gdx.graphics.getGL20().glClearColor( 0, 0, 0, 1 );
		Gdx.graphics.getGL20().glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
	}

	@Override
	public void resize(int width, int height)
	{

	}

	@Override
	public void show()
	{
		this.backgroundMusic.setLooping(true);
		this.backgroundMusic.play();
	}

	@Override
	public void hide()
	{

	}

	@Override
	public void pause()
	{
		this.backgroundMusic.pause();
	}

	@Override
	public void resume()
	{
		this.backgroundMusic.play();
	}

	@Override
	public void dispose()
	{
		this.backgroundMusic.stop();

		this.spriteBatch.dispose();
		this.touchpadSkin.dispose();
		this.stage.dispose();
		this.player.dispose();
		this.map.dispose();
		this.backgroundMusic.dispose();
		this.mapRenderer.dispose();
	}

	private void exit()
	{
		this.backButtonSound.setVolume(0.1f);
		this.backButtonSound.setOnCompletionListener(new Music.OnCompletionListener() {
			@Override
			public void onCompletion(Music music)
			{
			backButtonSound.dispose();
			}
		});
		this.backButtonSound.play();
		this.dispose();
		this.game.openMenu();
		Gdx.input.setCatchBackKey(false);
	}
}
