package de.lfstudios.game.view;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import de.lfstudios.game.Despair;

public class MenuScreen implements Screen
{

	private Despair game;
	private OrthographicCamera camera;
	private SpriteBatch spriteBatch;
	private Animation menuAnimation;
	private Texture menuSheet;
	private TextureRegion[] menuFrames;
	private TextureRegion currentFrame;
	private float stateTime;
	private Music backgroundMusic;
	private Sound startGameSound;
	private Music exitGameSound;

	public MenuScreen(Despair game)
	{
		this.game = game;

		this.camera = new OrthographicCamera();
		this.camera.setToOrtho(true, 1920, 1280);

		this.spriteBatch = new SpriteBatch();
		this.menuSheet = new Texture(Gdx.files.internal("menu/menuscreen_sheet.png"));

		TextureRegion[][] tmp = TextureRegion.split(this.menuSheet,
													this.menuSheet.getWidth() / 2,
													this.menuSheet.getHeight() / 2);
		this.menuFrames = new TextureRegion[4];
		this.menuFrames[0] = tmp[0][0];
		this.menuFrames[1] = tmp[0][1];
		this.menuFrames[2] = tmp[1][0];
		this.menuFrames[3] = tmp[1][1];
		this.menuAnimation = new Animation(0.5f, this.menuFrames);
		this.backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/menuscreen.mp3"));
		this.startGameSound = Gdx.audio.newSound(Gdx.files.internal("sounds/gong.mp3"));
		this.exitGameSound = Gdx.audio.newMusic(Gdx.files.internal("sounds/sword1.mp3"));
	}

	@Override
	public void render(float delta)
	{
		Gdx.graphics.getGL20().glClearColor( 0, 0, 0, 1 );
		Gdx.graphics.getGL20().glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);

		this.camera.update();
		this.stateTime += Gdx.graphics.getDeltaTime();
		this.currentFrame = this.menuAnimation.getKeyFrame(this.stateTime, true);
		this.spriteBatch.begin();
		this.spriteBatch.draw(this.currentFrame, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		this.spriteBatch.end();
		this.checkInput();
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
		this.menuSheet.dispose();
		this.spriteBatch.dispose();
		this.backgroundMusic.dispose();
		this.startGameSound.dispose();
		this.exitGameSound.dispose();
	}

	private void checkInput()
	{
		if(Gdx.input.justTouched())
		{
			if(Gdx.input.getX() > (Gdx.graphics.getWidth() / 3) && Gdx.input.getX() < ((Gdx.graphics.getWidth() / 3) * 2))
			{
				if(Gdx.input.getY() < (Gdx.graphics.getHeight() / 2) && Gdx.input.getY() > (Gdx.graphics.getHeight() / 4))
				{
					this.start();
				}
				else if (Gdx.input.getY() > (Gdx.graphics.getHeight() / 2) && Gdx.input.getY() < ((Gdx.graphics.getHeight() / 4) * 3))
				{
					this.exit();
				}
			}
		}
	}

	private void exit()
	{
		this.exitGameSound.setOnCompletionListener(new Music.OnCompletionListener()
		{
			@Override
			public void onCompletion(Music music)
			{
				game.exitGame();
			}
		});
		this.exitGameSound.setVolume(0.2f);
		this.exitGameSound.play();
		this.backgroundMusic.stop();
	}

	private void start()
	{
		this.startGameSound.play(0.1f);
		this.game.startGame();
		this.dispose();
	}
}
