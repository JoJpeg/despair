package de.lfstudios.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import de.lfstudios.game.controller.GameController;
import de.lfstudios.game.controller.MenuController;

/**
 * @author vwiebe
 */
public class Despair extends Game
{

	private MenuController menuController;
	private GameController gameController;

	@Override
	public void create()
	{
		this.menuController = new MenuController(this);
		this.setScreen(this.menuController.getMenuScreen());
	}

	public void startGame()
	{
		this.gameController = new GameController(this);
		this.setScreen(this.gameController.getGameScreen());
	}

	public void exitGame()
	{
		Gdx.app.exit();
	}

	public void openMenu()
	{
		this.create();
	}
}
