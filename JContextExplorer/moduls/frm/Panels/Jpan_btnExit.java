/*
 * Copyright (C) Justo Montiel, David Torres, Sergio Gomez, Alberto Fernandez
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, see
 * <http://www.gnu.org/licenses/>
 */

package moduls.frm.Panels;

import inicial.Language;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import moduls.frm.FrmPrincipalDesk;
import moduls.frm.children.AboutBox;

/**
 * <p>
 * <b>MultiDendrograms</b>
 * </p>
 *
 * About and Exit buttons
 *
 * @author Justo Montiel, David Torres, Sergio G&oacute;mez, Alberto Fern&aacute;ndez
 *
 * @since JDK 6.0
 */
public class Jpan_btnExit extends JPanel implements ActionListener {
	
	//components
	private static final long serialVersionUID = 1L;
	private JButton btnExit, btnInfo;
	private final FrmPrincipalDesk fr;
	private String sBtn1, sBtn2;

	private void CarregaIdioma() {
		sBtn1 = Language.getLabel(46); // exit
		sBtn2 = "Info";
	}

	public Jpan_btnExit(final FrmPrincipalDesk fr) {
		super();
		this.fr = fr;
		this.CarregaIdioma();
		this.getPanel();
		this.setVisible(true);
	}

	private void getPanel() {

		this.setLayout(new FlowLayout());
		this.setBorder(BorderFactory.createTitledBorder(""));

		// btn About
		btnInfo = new JButton(sBtn2);
		btnInfo.addActionListener(this);
		this.add(btnInfo);

		// btn Exit
		btnExit = new JButton(sBtn1);
		btnExit.addActionListener(this);
		this.add(btnExit);
	}

	@Override
	public void actionPerformed(final ActionEvent e) {
		if (e.getActionCommand().equals(sBtn1)) { // out
			fr.toGoOut();
		}
		else if (e.getActionCommand().equals(sBtn2)) { // info
			AboutBox a = new AboutBox(fr);
			a.setVisible(true);
		}
	}
}
