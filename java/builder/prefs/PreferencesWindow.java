/**
 *   This file is part of CReST: The Cloud Research Simulation Toolkit 
 *   Copyright (C) 2011, 2012 John Cartlidge 
 * 
 *   For a full list of contributors, refer to file CONTRIBUTORS.txt 
 *
 *   CReST was developed at the University of Bristol, UK, using 
 *   financial support from the UK's Engineering and Physical 
 *   Sciences Research Council (EPSRC) grant EP/H042644/1 entitled 
 *   "Cloud Computing for Large-Scale Complex IT Systems". Refer to
 *   <http://gow.epsrc.ac.uk/NGBOViewGrant.aspx?GrantRef=EP/H042644/1>
 * 
 *   CReST is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, either version 3 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with this program.  If not, see <http://www.gnu.org/licenses/gpl.txt>.
 *
 *   For further information, contact: 
 *
 *   Dr. John Cartlidge: john@john-cartlidge.co.uk
 *   Department of Computer Science,
 *   University of Bristol, The Merchant Venturers Building,
 *   Woodland Road, Bristol, BS8-1UB, United Kingdom.
 *
 */
package builder.prefs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import builder.MainWindow;
import builder.MainToolBar;

public class PreferencesWindow extends JDialog implements ActionListener
{
	// Serialize ID
	private static final long serialVersionUID = -5797126606137944119L;

	// Working variables
	private BuilderPreferences mPrefs;
	private MainWindow mWindow;

	// Control Buttons
	private JButton mOKButton;
	private JButton mApplyButton;
	private JButton mCloseButton;

	// ////////////////////////
	// Card Option Controls //
	// ////////////////////////

	// User Option controls
	private JCheckBox mShowWelcomeScreenOnStartup;

	// Run Card controls
	private JTextField jarLocText;
	private JButton jarLocButton;
	private JTextField javaComText;
	private JTextField javaArgsText;
	
	// Toolbar Panel
	private ToolbarOptionPanel mToolBarPanel;

	// /////////////////////
	// Card Layout Setup //
	// /////////////////////

	// Layout card names
	private JPanel mCard;
	private CardLayout mCardLayout;
	public static final String USER_PREFS_NAME = "User";
	public static final String RUN_PREFS_NAME = "Run";
	public static final String TOOLBAR_PREFS_NAME = "Toolbar";
	// Define our starting panel
	public static final String DEFAULT_STARTING_CARD = USER_PREFS_NAME;

	// Option buttons
	OptionTile optButons[] = new OptionTile[3];

	public PreferencesWindow(BuilderPreferences prefs, MainWindow parent,
			String startCard)
	{
		super(parent.getFrame(), true);
		setTitle(prefs.getProgramName() + " Preferences");
		mPrefs = prefs;
		mWindow = parent;

		// Setup window
		this.setIconImage(new ImageIcon(mPrefs.getIconResourcePath()
				+ "preferences.png").getImage());

		this.add(setupSwitcherPanel(), BorderLayout.NORTH);
		this.add(setupButtonPanel(), BorderLayout.SOUTH);
		mCard = setupCardPanel();
		this.add(mCard, BorderLayout.CENTER);

		setSize(new Dimension(500, 400));
		this.setLocationRelativeTo(parent.getFrame());
		this.setResizable(false);
		changeCard(startCard);
		this.setVisible(true);
	}

	public PreferencesWindow(BuilderPreferences prefs, MainWindow parent)
	{
		this(prefs, parent, DEFAULT_STARTING_CARD);
	}

	private JPanel setupSwitcherPanel()
	{
		JPanel pan = new JPanel();
		pan.setLayout(new FlowLayout());

		// create buttons
		optButons[0] = new OptionTile(USER_PREFS_NAME, new ImageIcon(
				mPrefs.getIconResourcePath() + "user_config_big.png"));
		optButons[1] = new OptionTile(RUN_PREFS_NAME, new ImageIcon(
				mPrefs.getIconResourcePath() + "run_config_big.png"));
		optButons[2] = new OptionTile(TOOLBAR_PREFS_NAME, new ImageIcon(
				mPrefs.getIconResourcePath() + "toolbar_config_big.png"));

		pan.add(optButons[0]);
		pan.add(Box.createHorizontalStrut(20));
		pan.add(optButons[1]);
		pan.add(Box.createHorizontalStrut(20));
		pan.add(optButons[2]);

		pan.setBackground(Color.WHITE);
		// pan.setBorder(new javax.swing.border.LineBorder(Color.BLACK));
		return pan;
	}

	private class OptionTile extends JButton implements ActionListener
	{
		private static final long serialVersionUID = -5942404729591159297L;
		// The name of the card in the mLayout (a CardLayout) that we should
		// represent
		private String cardName;
		private JLabel selectMark;

		public OptionTile(String cName, ImageIcon icon)
		{
			super(cName);
			// Make text display at the bottom of the button
			setVerticalTextPosition(SwingConstants.BOTTOM);
			setHorizontalTextPosition(SwingConstants.CENTER);
			cardName = cName;
			addActionListener(this);
			this.setIcon(icon);
			this.setToolTipText(cName + " Preferences");
			this.setPreferredSize(new Dimension(85, 61));

			selectMark = new JLabel(new ImageIcon(mPrefs.getIconResourcePath()
					+ "select_mark.png"));
			this.add(selectMark);

			// Make the button clear
			setFocusPainted(false);
			setContentAreaFilled(false);
			setBorderPainted(false);
			setOpaque(false);

		}

		/**
		 * Called by the PreferencesWindow when the card is changed
		 */
		public void updatedCard(String newCardName)
		{
			selectMark.setVisible(newCardName.equals(cardName));
		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO Auto-generated method stub
			changeCard(cardName);

		}

	}

	private JPanel setupButtonPanel()
	{
		// Create panel
		JPanel pan = new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.LINE_AXIS));

		// Create buttons
		mOKButton = new JButton("OK");
		mOKButton.addActionListener(this);

		mApplyButton = new JButton("Apply");
		mApplyButton.addActionListener(this);

		mCloseButton = new JButton("Cancel");
		mCloseButton.addActionListener(this);

		// Layout
		pan.add(mCloseButton);
		pan.add(Box.createHorizontalGlue());
		// pan.add(Box.createHorizontalStrut(10));
		pan.add(mApplyButton);
		pan.add(Box.createHorizontalStrut(10));
		pan.add(mOKButton);
		pan.setBorder(new EmptyBorder(10, 10, 10, 10));

		return pan;
	}

	private JPanel setupCardPanel()
	{
		JPanel pan = new JPanel();
		mCardLayout = new CardLayout();
		pan.setLayout(mCardLayout);
		mToolBarPanel = new ToolbarOptionPanel();

		// Add cards
		pan.add(setupUserOptionPanel(), USER_PREFS_NAME);
		pan.add(setupRunOptionPanel(), RUN_PREFS_NAME);
		pan.add(mToolBarPanel, TOOLBAR_PREFS_NAME);

		return pan;
	}

	private JPanel setupUserOptionPanel()
	{
		JPanel pan = new JPanel();

		mShowWelcomeScreenOnStartup = new JCheckBox(
				"Show Welcome Screen on startup", mPrefs.getShowWelcomeScreen());
		mShowWelcomeScreenOnStartup.addActionListener(this);
		mShowWelcomeScreenOnStartup.setBorder(new EmptyBorder(10, 10, 10, 10));

		pan.add(mShowWelcomeScreenOnStartup);

		return pan;
	}

	private JPanel setupRunOptionPanel()
	{
		JPanel pan = new JPanel();
		pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));

		// Jar location
		{
			JPanel panJarLoc = new JPanel();
			panJarLoc.setLayout(new BorderLayout());

			JLabel jarLocLabel = new JLabel("CloudSIM jarfile location");
			jarLocLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
			panJarLoc.add(jarLocLabel, BorderLayout.WEST);

			jarLocText = new JTextField(mPrefs.getJarFileLocation());
			panJarLoc.add(jarLocText, BorderLayout.CENTER);

			jarLocButton = new JButton();
			jarLocButton.setIcon(mPrefs.getImage("open_jarfile.png"));
			panJarLoc.add(jarLocButton, BorderLayout.EAST);
			jarLocButton.setPreferredSize(new Dimension(20, 20));
			jarLocButton.addActionListener(new FileViewerHandler());

			panJarLoc.setMaximumSize(new Dimension(Short.MAX_VALUE, 10));
			panJarLoc.setBorder(new EmptyBorder(10, 10, 5, 10));
			pan.add(panJarLoc);
		}

		// Java Location
		{
			JPanel panJavaLoc = new JPanel();
			panJavaLoc.setLayout(new BorderLayout());

			JLabel javaLabel = new JLabel("Java Runtime Enviroment command");
			javaLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
			panJavaLoc.add(javaLabel, BorderLayout.WEST);

			javaComText = new JTextField(mPrefs.getJavaCommand());
			panJavaLoc.add(javaComText, BorderLayout.CENTER);

			panJavaLoc.setMaximumSize(new Dimension(Short.MAX_VALUE, 10));
			panJavaLoc.setBorder(new EmptyBorder(5, 10, 5, 10));
			pan.add(panJavaLoc);
		}

		// JRE arguments
		{
			JPanel panJavaLoc = new JPanel();
			panJavaLoc.setLayout(new BorderLayout());

			JLabel javaLabel = new JLabel("Java Runtime Enviroment arguments");
			javaLabel.setBorder(new EmptyBorder(0, 0, 0, 10));
			panJavaLoc.add(javaLabel, BorderLayout.WEST);

			javaArgsText = new JTextField(mPrefs.getJavaArgs());
			panJavaLoc.add(javaArgsText, BorderLayout.CENTER);

			panJavaLoc.setMaximumSize(new Dimension(Short.MAX_VALUE, 10));
			panJavaLoc.setBorder(new EmptyBorder(5, 10, 5, 10));
			pan.add(panJavaLoc);
		}

		pan.add(Box.createVerticalGlue());
		return pan;
	}

	private class ToolbarOptionPanel extends JPanel implements ActionListener,
			ListSelectionListener
	{
		private static final long serialVersionUID = -8522481595091762583L;

		private JList masterList;
		private DefaultListModel masterListModel;
		private JList usedList;
		private DefaultListModel usedListModel;

		private JButton addButton;
		private JButton delButton;
		private JButton upButton;
		private JButton downButton;

		public ToolbarOptionPanel()
		{
			setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
			setBorder(new EmptyBorder(10, 10, 10, 10));

			// Setup Lists
			JPanel masterListPanel = new JPanel();
			masterListPanel.setLayout(new BoxLayout(masterListPanel,
					BoxLayout.PAGE_AXIS));
			masterListPanel.add(new JLabel("Available Components"));
			masterListPanel.add(Box.createVerticalStrut(10));
			masterListModel = new DefaultListModel();
			masterList = new JList(masterListModel);
			masterList.addListSelectionListener(this);
			masterList
					.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			masterList.setVisibleRowCount(-1);
			JScrollPane cpListScroller = new JScrollPane(masterList);
			cpListScroller.setPreferredSize(new Dimension(250, 80));
			masterListPanel.add(cpListScroller);
			add(masterListPanel);

			add(Box.createHorizontalStrut(10));

			// setup buttons
			add(getMidPanel());

			add(Box.createHorizontalStrut(10));

			// Setup user list
			JPanel userListPanel = new JPanel();
			userListPanel.setLayout(new BoxLayout(userListPanel,
					BoxLayout.PAGE_AXIS));
			userListPanel.add(new JLabel("Current Components"));
			userListPanel.add(Box.createVerticalStrut(10));
			usedListModel = new DefaultListModel();
			usedList = new JList(usedListModel);
			usedList.addListSelectionListener(this);
			usedList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
			usedList.setVisibleRowCount(-1);
			JScrollPane userListScroller = new JScrollPane(usedList);
			userListScroller.setPreferredSize(new Dimension(250, 80));
			userListPanel.add(userListScroller);
			userListPanel.add(Box.createVerticalStrut(10));
			userListPanel.add(getLowerPanel());
			add(userListPanel);

			populateLists();
			updateEnabled();

		}

		private void populateLists()
		{
			for (String s : MainToolBar.BUTTON_NAMES)
			{
				masterListModel.addElement(s);

			}
			for (String s : MainToolBar.buttonValuesToNames(mPrefs
					.getToolBarLayout().split(";")))
			{
				usedListModel.addElement(s);
			}

		}

		private JPanel getMidPanel()
		{
			// Setups buttons
			addButton = new JButton(mPrefs.getImage("toolbar_add.png"));
			addButton.addActionListener(this);
			delButton = new JButton(mPrefs.getImage("toolbar_del.png"));
			delButton.addActionListener(this);

			JPanel pan = new JPanel();
			pan.setLayout(new BoxLayout(pan, BoxLayout.PAGE_AXIS));
			pan.add(Box.createVerticalGlue());
			pan.add(addButton);
			pan.add(Box.createVerticalStrut(20));
			pan.add(delButton);
			pan.add(Box.createVerticalGlue());

			return pan;
		}

		private JPanel getLowerPanel()
		{
			// Setups buttons
			upButton = new JButton(mPrefs.getImage("move_up.png"));
			upButton.addActionListener(this);
			downButton = new JButton(mPrefs.getImage("move_down.png"));
			downButton.addActionListener(this);

			JPanel pan = new JPanel();
			pan.setLayout(new BoxLayout(pan, BoxLayout.LINE_AXIS));
			pan.add(Box.createHorizontalGlue());
			pan.add(upButton);
			pan.add(Box.createHorizontalGlue());
			pan.add(downButton);
			pan.add(Box.createHorizontalGlue());
			// pan.setBackground(Color.red);

			return pan;
		}

		/**
		 * Checks the state of the lists to see what buttons should be enabled
		 * or disabled.
		 */
		private void updateEnabled()
		{
			int mIndex = masterList.getSelectedIndex();
			int uIndex = usedList.getSelectedIndex();

			// Up/Down buttons
			if (uIndex != -1)
			{
				delButton.setEnabled(true);
				// We have *something* selected
				upButton.setEnabled(uIndex != 0);
				downButton
						.setEnabled(uIndex != (usedList.getModel().getSize() - 1));
			} else
			{
				delButton.setEnabled(false);
				upButton.setEnabled(false);
				downButton.setEnabled(false);
			}

			// add Button
			addButton.setEnabled(mIndex != -1);

		}

		@Override
		public void actionPerformed(ActionEvent e)
		{
			// TODO Button response code --James
			Object source = e.getSource();
			if (source.equals(addButton))
			{
				int selected = usedList.getSelectedIndex();
				if(selected != -1)
				{
					usedListModel.insertElementAt(masterList.getSelectedValue(), selected+1);
					usedList.setSelectedIndex(selected+1);
				}
				else
				{
					usedListModel.addElement(masterList.getSelectedValue());
				}
				
			}else if(source.equals(delButton))
			{
				// We know the usedList must be selected
				int selected = usedList.getSelectedIndex();
				usedListModel.remove(usedList.getSelectedIndex());
				if(selected >= usedListModel.size()){
					selected = usedListModel.size()-1;
				}
				usedList.setSelectedIndex(selected);
			}else if(source.equals(upButton))
			{
				int selected = usedList.getSelectedIndex();
				Object obj = usedListModel.elementAt(selected);
				usedListModel.remove(selected);
				selected--;
				usedListModel.insertElementAt(obj, selected);
				usedList.setSelectedIndex(selected);
			}else if(source.equals(downButton))
			{
				int selected = usedList.getSelectedIndex();
				Object obj = usedListModel.elementAt(selected);
				usedListModel.remove(selected);
				selected++;
				usedListModel.insertElementAt(obj, selected);
				usedList.setSelectedIndex(selected);
			}

			
			// Update buttons
			updateEnabled();
		}

		@Override
		public void valueChanged(ListSelectionEvent e)
		{
			updateEnabled();
		}
		
		public String getSaveString()
		{
			// Get all elements
			String returns = "";
			for(int i=0;i<usedListModel.size();i++)
			{
				returns += MainToolBar.buttonNamesToValues((String) usedListModel.get(i)) + ";";
			}
			return returns;
			
		}

	}

	private void changeCard(String newCard)
	{
		// Update the card
		mCardLayout.show(mCard, newCard);

		// Notify all of the buttons that the active card has changed
		for (OptionTile b : optButons)
		{
			b.updatedCard(newCard);
		}
	}

	/**
	 * Saves all the settings
	 */
	private void save()
	{
		// User Card
		mPrefs.setShowWelcomeScreen(this.mShowWelcomeScreenOnStartup
				.isSelected());

		// Run Card
		mPrefs.setJarFileLocation(this.jarLocText.getText());
		mPrefs.setJavaArgs(this.javaArgsText.getText());
		mPrefs.setJavaCommand(this.javaComText.getText());
		
		// Toolbar card
		mPrefs.setToolBarLayout(mToolBarPanel.getSaveString());
		mWindow.redrawToolbar();
	}

	@Override
	public void actionPerformed(ActionEvent e)
	{
		// Bar buttons below
		if (e.getSource().equals(mCloseButton))
		{
			this.dispose();
		} else if (e.getSource().equals(mApplyButton))
		{
			save();
		} else if (e.getSource().equals(mOKButton))
		{
			save();
			this.dispose();
		}
	}

	private class FileViewerHandler implements ActionListener
	{

		@Override
		public void actionPerformed(ActionEvent e)
		{
			JFileChooser fc = new JFileChooser();
			int rVal = fc.showOpenDialog(null);

			fc.setAcceptAllFileFilterUsed(false);
			FileFilter filter = new FileNameExtensionFilter(
					mPrefs.getProgramName() + " JAR file", "jar");
			fc.addChoosableFileFilter(filter);

			if (rVal == JFileChooser.APPROVE_OPTION)
			{
				jarLocText.setText(fc.getSelectedFile().getAbsolutePath());
			}
		}
	}
}
