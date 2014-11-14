package de.lfstudios.game.controller;

import de.lfstudios.game.Despair;
import de.lfstudios.game.model.MenuModel;
import de.lfstudios.game.view.MenuScreen;

public class MenuController
{
	private Despair game;
	private MenuModel menuModel;
	private MenuScreen menuScreen;

	/**
	 *
	 * @param game
	 */
	public MenuController(Despair game)
	{
		this.game = game;
		this.menuModel = new MenuModel();
		this.menuScreen = new MenuScreen(this.game);
	}

	/**
	 *
	 * @return
	 */
	public MenuModel getMenuModel()
	{
		return this.menuModel;
	}

	/**
	 *
	 * @param menuModel
	 */
	public void setMenuModel(MenuModel menuModel)
	{
		this.menuModel = menuModel;
	}

	/**
	 *
	 * @return
	 */
	public MenuScreen getMenuScreen()
	{
		return this.menuScreen;
	}

	/**
	 *
	 * @param menuScreen
	 */
	public void setMenuScreen(MenuScreen menuScreen)
	{
		this.menuScreen = menuScreen;
	}

}