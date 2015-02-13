package de.lfstudios.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Touchpad;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import de.lfstudios.game.Despair;
import de.lfstudios.game.core.map.Map;
import de.lfstudios.game.core.player.Player;

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
	private Map map;
	private Player player;

	private ImageButton attackButton;
	private Skin attackButtonSkin;
	private ImageButton.ImageButtonStyle attackButtonStyle;
	private Drawable attackButtonDrawable;
	private Drawable attackButtonActiveDrawable;
	private ImageButton blockButton;
	private Skin blockButtonSkin;
	private ImageButton.ImageButtonStyle blockButtonStyle;
	private Drawable blockButtonDrawable;
	private Drawable blockButtonActiveDrawable;

//	private ShapeRenderer sr = new ShapeRenderer();

	public GameScreen(Despair game)
	{
		this.game = game;

		this.aspectRatio = (float) Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight();

		this.camera = new OrthographicCamera(aspectRatio , 1);
		this.camera.position.set(0, 0, 0);
		this.camera.update();

		this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/ingame_light.mp3"));
		this.backButtonSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/gong.mp3"));
		this.backgroundMusic.play();

		this.spriteBatch = new SpriteBatch();
		Gdx.input.setCatchBackKey(true);

		this.stage = new Stage(new ScreenViewport(this.camera), this.spriteBatch);
		this.uiStage = new Stage(new ExtendViewport(UI_HEIGHT * aspectRatio , UI_HEIGHT));

		this.map = new Map();
		this.player = new Player();
		this.player.setPosition(777 * this.map.getMapScale(), 3820 * this.map.getMapScale());

		this.map.add(this.player);

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
		this.attackButtonSkin.add("inactive", new Texture(Gdx.files.internal("game/button_atk.png")));
		this.attackButtonSkin.add("active", new Texture(Gdx.files.internal("game/button_atk_a.png")));
		this.attackButtonStyle = new ImageButton.ImageButtonStyle();
		this.attackButtonDrawable = this.attackButtonSkin.getDrawable("inactive");
		this.attackButtonActiveDrawable = this.attackButtonSkin.getDrawable("active");
		this.attackButtonStyle.imageUp = this.attackButtonDrawable;
		this.attackButtonStyle.imageDown = this.attackButtonActiveDrawable;
		this.attackButton = new ImageButton(this.attackButtonStyle);
		this.attackButton.setBounds(UI_HEIGHT * aspectRatio - UI_CONTROL_AREA_SIZE / 2 - this.attackButton.getWidth() * 2f - UI_VIEW_BORDER,
				UI_CONTROL_AREA_SIZE / 2 + UI_VIEW_BORDER - this.touchKnob.getMinWidth() / 2,
				this.attackButton.getHeight(),
				this.attackButton.getWidth());

		this.attackButton.addListener( new InputListener()
		{
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
			{
				player.attack();
				return true;
			}

			public void touchUp (InputEvent event, float x, float y, int pointer, int button)
			{

			}
		});

		this.blockButtonSkin = new Skin();
		this.blockButtonSkin.add("inactive", new Texture(Gdx.files.internal("game/button_def.png")));
		this.blockButtonSkin.add("active", new Texture(Gdx.files.internal("game/button_def_a.png")));
		this.blockButtonStyle = new ImageButton.ImageButtonStyle();
		this.blockButtonDrawable = this.blockButtonSkin.getDrawable("inactive");
		this.blockButtonActiveDrawable = this.blockButtonSkin.getDrawable("active");
		this.blockButtonStyle.imageUp = this.blockButtonDrawable;
		this.blockButtonStyle.imageDown = this.blockButtonActiveDrawable;
		this.blockButton = new ImageButton(this.blockButtonStyle);
		this.blockButton.setBounds(UI_HEIGHT * aspectRatio - UI_CONTROL_AREA_SIZE / 2 - UI_VIEW_BORDER,
				UI_CONTROL_AREA_SIZE / 2 + UI_VIEW_BORDER,
				this.blockButton.getHeight(),
				this.blockButton.getWidth());

		this.blockButton.addListener( new InputListener()
		{
			public boolean touchDown (InputEvent event, float x, float y, int pointer, int button)
			{
				player.block();
				return true;
			}

			public void touchUp (InputEvent event, float x, float y, int pointer, int button)
			{
				player.setBlockReleased(true);
			}
		});
		
		this.uiStage.addActor(this.touchpad);
		this.uiStage.addActor(this.attackButton);
		this.uiStage.addActor(this.blockButton);
		Gdx.input.setInputProcessor(this.uiStage);
	}

	@Override
	public void render(float delta)
	{
		this.clearScreen();
		this.updatePlayer();
		this.updateMap();
		this.updateCamera();

		this.stage.draw();
		this.uiStage.draw();

		if (Gdx.input.isKeyPressed(Input.Keys.BACK))
		{
			this.exit();
		}


//		int tileWidth = 128 * 4;
//		int mapWidth = 4096 * 4;
//		sr.setProjectionMatrix(camera.combined);
//		sr.begin(ShapeRenderer.ShapeType.Line);
//
//		for (int x = 0; x < mapWidth; x += tileWidth)
//		{
//			sr.line(x, 0, x, 4096 * 4);
//		}
//
//
//		for (int y = 0; y < 4096 * 4; y += 128 * 4)
//		{
//			sr.line(0, y, mapWidth, y);
//		}
//
//		sr.end();
	}

	private void updateMap()
	{
		this.map.getMapRenderer().setView(this.camera);
		this.map.getMapRenderer().render();
		this.map.updatePhysics(this.camera);

		this.map.draw(this.spriteBatch, this.player, new Vector2(this.camera.position.x, this.camera.position.y));
	}

	private void updatePlayer()
	{
		this.player.update(this.touchpad);
		this.player.setPosition(this.player.getBody().getPosition().x * this.map.getBoxToWorld(),
								this.player.getBody().getPosition().y * this.map.getBoxToWorld());
//		this.player.draw(this.spriteBatch);
	}

	private void updateCamera()
	{
		this.camera
			.position.set(this.player.getPosX() + (this.player.getCurrentFrame().getRegionWidth() / 2),
						  this.player.getPosY() + (this.player.getCurrentFrame().getRegionHeight() / 2),
						  0);

		float camerax1 = this.camera.position.x - (this.camera.viewportWidth / 2);
		float camerax2 = this.camera.position.x + (this.camera.viewportWidth / 2);
		float cameray1 = this.camera.position.y - (this.camera.viewportHeight / 2);
		float cameray2 = this.camera.position.y + (this.camera.viewportHeight / 2);

		if(camerax1 < 0) this.camera.position.set(this.camera.viewportWidth / 2, this.camera.position.y, 0);
		if(cameray1 < 0) this.camera.position.set(this.camera.position.x, this.camera.viewportHeight / 2, 0);
		if(camerax2 > this.map.getMapPixelWidth()) this.camera.position.set(this.map.getMapPixelWidth() - (this.camera.viewportWidth / 2), this.camera.position.y, 0);
		if(cameray2 > this.map.getMapPixelHeight()) this.camera.position.set(this.camera.position.x, this.map.getMapPixelHeight() - (this.camera.viewportHeight / 2), 0);

		this.camera.update();
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
		this.backgroundMusic.dispose();
		this.map.dispose();
	}

	private void exit()
	{
		this.backButtonSound.setVolume(0.1f);
		this.backButtonSound.setOnCompletionListener(new Music.OnCompletionListener()
		{
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
