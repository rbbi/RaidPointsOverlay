/*
 * Copyright (c) 2018, Kamiel
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package trevor.raidpointsoverlay;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.text.DecimalFormat;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.Point;
import net.runelite.api.Varbits;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayPriority;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.PanelComponent;
import net.runelite.client.ui.overlay.tooltip.Tooltip;
import net.runelite.client.ui.overlay.tooltip.TooltipManager;

public class RaidsPointsOverlay extends Overlay
{
	private static final DecimalFormat POINTS_FORMAT = new DecimalFormat("#,###");

	private Client client;
	private RaidPointsOverlayPlugin plugin;
	private  RaidsPointsConfig config;
	private TooltipManager tooltipManager;

	private final PanelComponent panel = new PanelComponent();

	@Inject
	private RaidsPointsOverlay(Client client,
							   RaidPointsOverlayPlugin plugin,
							   RaidsPointsConfig config,
							   TooltipManager tooltipManager)
	{
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		this.tooltipManager = tooltipManager;
		setPosition(OverlayPosition.TOP_RIGHT);
		setPriority(OverlayPriority.HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!plugin.isInRaidChambers())
		{
			return null;
		}

		int totalPoints = client.getVar(Varbits.TOTAL_POINTS);
		int personalPoints = client.getVar(Varbits.PERSONAL_POINTS);
		int partySize = client.getVar(Varbits.RAID_PARTY_SIZE);

		panel.getChildren().clear();
		panel.getChildren().add(LineComponent.builder()
			.left("Total:")
			.right(POINTS_FORMAT.format(totalPoints))
			.build());

		panel.getChildren().add(LineComponent.builder()
			.left(client.getLocalPlayer().getName() + ":")
			.right(POINTS_FORMAT.format(personalPoints))
			.build());

		if (config.raidsTimer())
		{
			panel.getChildren().add(LineComponent.builder()
				.left("Time:")
				.right(plugin.getTime())
				.build());
		}

		if (partySize > 1 && config.showTeamSize())
		{
			panel.getChildren().add(LineComponent.builder()
				.left("Party size:")
				.right(String.valueOf(partySize))
				.build());
		}

		final Rectangle bounds = this.getBounds();
		if (bounds.getX() > 0)
		{
			final Point mousePosition = client.getMouseCanvasPosition();

			if (bounds.contains(mousePosition.getX(), mousePosition.getY()))
			{
				String tooltip = plugin.getTooltip();

				if (tooltip != null)
				{
					tooltipManager.add(new Tooltip(tooltip));
				}
			}
		}

		return panel.render(graphics);
	}
}
