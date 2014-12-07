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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.lfstudios.game.Despair;
import de.lfstudios.game.core.Player;

public class GameScreen implements Screen
{
	private static final float UI_HEIGHT = 200;
	private static final float UI_VIEW_BORDER = UI_HEIGHT / 40;
	private static final float UI_CONTROL_AREA_SIZE = (UI_HEIGHT / 8) * 3;
	private static final float VIEW_HEIGHT = 200;
	private float aspectRatio;

	private Stage uiStage;
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

		this.aspectRatio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();

		this.camera = new OrthographicCamera(aspectRatio , 1);
		this.camera.position.set(0, 0, 0);
		this.camera.update();

		this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/ingame.mp3"));
		this.backButtonSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/gong.mp3"));
		this.backgroundMusic.play();

		this.spriteBatch = new SpriteBatch();
		Gdx.input.setCatchBackKey(true);

		this.stage = new Stage(new ScreenViewport(this.camera), this.spriteBatch);
		this.uiStage = new Stage(new ExtendViewport(UI_HEIGHT * aspectRatio , UI_HEIGHT));

		this.setupMap();

		this.player = new Player();
		this.player.setPosition(192 * 5, 416 * 5);

		this.touchpadSkin = new Skin();
		this.touchpadSkin.add("knob", new Texture(Gdx.files.internal("game/knob.png")));
		this.touchpadStyle = new Touchpad.TouchpadStyle();
		this.touchKnob = this.touchpadSkin.getDrawable("knob");
		this.touchpadStyle.knob = this.touchKnob;
		this.touchpad = new Touchpad(0, this.touchpadStyle);
		this.touchpad.setBounds(UI_VIEW_BORDER,
				UI_VIEW_BORDER,
				UI_CONTROL_AREA_SIZE,
				UI_CONTROL_AREA_SIZE);

		this.attackButtonSkin = new Skin();
		this.attackButtonSkin.add("icon", new Texture(Gdx.files.internal("game/button_atk.png")));
		this.attackButtonStyle = new ImageButton.ImageButtonStyle();
		this.attackButtonDrawable = this.attackButtonSkin.getDrawable("icon");
		this.attackButtonStyle.imageUp = this.attackButtonDrawable;
		this.attackButton = new ImageButton(this.attackButtonStyle);
		this.attackButton.setBounds(UI_HEIGHT * aspectRatio - UI_CONTROL_AREA_SIZE / 2 - this.attackButton.getWidth() * 2f - UI_VIEW_BORDER,
				UI_CONTROL_AREA_SIZE / 2 + UI_VIEW_BORDER - this.touchKnob.getMinWidth() / 2,
				this.attackButton.getHeight(),
				this.attackButton.getWidth());

		this.blockButtonSkin = new Skin();
		this.blockButtonSkin.add("icon", new Texture(Gdx.files.internal("game/button_def.png")));
		this.blockButtonStyle = new ImageButton.ImageButtonStyle();
		this.blockButtonDrawable = this.blockButtonSkin.getDrawable("icon");
		this.blockButtonStyle.imageUp = this.blockButtonDrawable;
		this.blockButton = new ImageButton(this.blockButtonStyle);
		this.blockButton.setBounds(UI_HEIGHT * aspectRatio - UI_CONTROL_AREA_SIZE / 2 - UI_VIEW_BORDER,
				UI_CONTROL_AREA_SIZE / 2 + UI_VIEW_BORDER,
				this.blockButton.getHeight(),
				this.blockButton.getWidth());

		this.uiStage.addActor(this.touchpad);
		this.uiStage.addActor(this.attackButton);
		this.uiStage.addActor(this.blockButton);
		Gdx.input.setInputProcessor(this.uiStage);
	}

	private void setupMap()
	{
		this.map = new TmxMapLoader().load("game/map/scene1.tmx");
		this.tileSet =  this.map.getTileSets().getTileSet("ground");
		this.mapRenderer = new OrthogonalTiledMapRenderer(this.map, (float) Gdx.graphics.getHeight() / VIEW_HEIGHT );
	}

	@Override
	public void render(float delta)
	{
		this.clearScreen();
		this.updateMap();
		this.updatePlayer();
		this.updateCamera();

		//draw
		this.spriteBatch.begin();
		this.spriteBatch.draw(this.player.getCurrentFrame(),
							  this.player.getPosX(),
							  this.player.getPosY(),
							  this.player.getCurrentFrame().getRegionWidth() * (float) Gdx.graphics.getHeight() / VIEW_HEIGHT ,
							  this.player.getCurrentFrame().getRegionHeight() * (float) Gdx.graphics.getHeight() / VIEW_HEIGHT );
		this.spriteBatch.end();

		this.stage.draw();
		this.uiStage.draw();


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
