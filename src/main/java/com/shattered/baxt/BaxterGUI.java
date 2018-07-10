package com.shattered.baxt;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.Font;

public class BaxterGUI {

	public Baxter baxter = new Baxter(this);

	private JFrame frame;
	private JTextField txtTrimLength;
	private JList<File> fileList;
	private JScrollPane scrollPane;
	private JPanel filePanel;
	private JTable table;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BaxterGUI window = new BaxterGUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public BaxterGUI() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JMenuBar menuBar = new JMenuBar();
		frame.getContentPane().add(menuBar, BorderLayout.NORTH);

		JMenu mnOptMenu = new JMenu("Options");
		mnOptMenu.setHorizontalAlignment(SwingConstants.LEFT);
		menuBar.add(mnOptMenu);

		JMenuItem mntmIOOpt = new JMenuItem("File Specs");
		mnOptMenu.add(mntmIOOpt);

		JPanel srcPanel = new JPanel();
		frame.getContentPane().add(srcPanel, BorderLayout.CENTER);
		srcPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JPanel TITLE = new JPanel();
		srcPanel.add(TITLE);

		JLabel lblBaxter = new JLabel("BAXTER");
		lblBaxter.setFont(new Font("NanumMyeongjo", Font.BOLD, 24));
		TITLE.add(lblBaxter);

		JPanel buttonPanel = new JPanel();
		srcPanel.add(buttonPanel);

		JButton btnAddVideos = new JButton("Add Videos");
		btnAddVideos.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				baxter.addVideos();
			}
		});
		buttonPanel.add(btnAddVideos);

		JButton btnRemoveVideos = new JButton("Remove Videos");
		btnRemoveVideos.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				baxter.removeVideos(fileList.getSelectedIndices());
			}
		});
		buttonPanel.add(btnRemoveVideos);

		JButton btnRenderBaxt = new JButton("Render Baxt");
		btnRenderBaxt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				baxter.renderVids(txtTrimLength.getText());
			}
		});
		buttonPanel.add(btnRenderBaxt);

		JPanel optionPanel = new JPanel();
		srcPanel.add(optionPanel);

		JLabel lbltrimLength = new JLabel("Trim Length (secs)");
		optionPanel.add(lbltrimLength);

		txtTrimLength = new JTextField();
		optionPanel.add(txtTrimLength);
		txtTrimLength.setText("3");
		txtTrimLength.setColumns(5);

		filePanel = new JPanel();
		filePanel.add(new JScrollPane(new JList<Object>()));
		srcPanel.add(filePanel);

		table = new JTable();
		filePanel.add(table);
		mntmIOOpt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				JFrame optionFrame = new JFrame();
				optionFrame.setBounds(100, 100, 500, 550);
				JPanel optpanel = new JPanel();
				optionFrame.getContentPane().add(optpanel, BorderLayout.NORTH);
				optpanel.setLayout(new GridLayout(21, 2, 0, 0));



				JLabel lblHasAudio = new JLabel("Has Audio");
				optpanel.add(lblHasAudio);

				Checkbox fld = new Checkbox();
				fld.setState(true);
				optpanel.add(fld);

				JLabel lblHasVideo = new JLabel("Has Video");
				optpanel.add(lblHasVideo);

				Checkbox fld_1 = new Checkbox( );
				fld_1.setState(true);
				optpanel.add(fld_1);

				JLabel lblRealTime = new JLabel("Real Time");
				optpanel.add(lblRealTime);

				Checkbox fld_2 = new Checkbox( );
				optpanel.add(fld_2);

				JLabel lblACodec = new JLabel("Audio Codec");
				optpanel.add(lblACodec);

				JTextField fld_3 = new JTextField( );
				optpanel.add(fld_3);
				fld_3.setText(null);

				JLabel lblVCodec = new JLabel("Video Codec");
				optpanel.add(lblVCodec);

				JTextField fld_4 = new JTextField( );
				optpanel.add(fld_4);
				fld_4.setText(null);

				JLabel lblContainerFormat = new JLabel("Container Format");
				optpanel.add(lblContainerFormat);

				JTextField fld_5 = new JTextField( );
				optpanel.add(fld_5);


				JLabel lblAStream = new JLabel("Audio Stream");
				optpanel.add(lblAStream);

				JTextField fld_6 = new JTextField( );
				optpanel.add(fld_6);

				JLabel lblAQuality = new JLabel("Audio Quality");
				optpanel.add(lblAQuality);

				JTextField fld_7 = new JTextField( );
				optpanel.add(fld_7);

				JLabel lblSampleRate = new JLabel("Sample Rate");
				optpanel.add(lblSampleRate);

				JTextField fld_8 = new JTextField( );
				optpanel.add(fld_8);

				JLabel Channels = new JLabel("Channels");
				optpanel.add(Channels);

				JTextField fld_9 = new JTextField( );
				optpanel.add(fld_9);

				JLabel lblABitrate = new JLabel("Audio Bitrate");
				optpanel.add(lblABitrate);

				JTextField fld_10 = new JTextField( );
				optpanel.add(fld_10);

				JLabel lblVBitrate = new JLabel("Video Bitrate");
				optpanel.add(lblVBitrate);

				JTextField fld_11 = new JTextField( );
				optpanel.add(fld_11);

				JLabel lblVBitrateTolerance = new JLabel("Video Bitrate Tolerance");
				optpanel.add(lblVBitrateTolerance);

				JTextField fld_12 = new JTextField( );
				optpanel.add(fld_12);

				JLabel lblVQuality = new JLabel("Video Quality");
				optpanel.add(lblVQuality);

				JTextField fld_13 = new JTextField( );
				optpanel.add(fld_13);

				JLabel lblVStream = new JLabel("Video Stream");
				optpanel.add(lblVStream);

				JTextField fld_14 = new JTextField( );
				optpanel.add(fld_14);

				JLabel lblVScaleFactor = new JLabel("Scale Factor");
				optpanel.add(lblVScaleFactor);

				JTextField fld_15 = new JTextField( );
				optpanel.add(fld_15);

				JLabel lblIContainerFormat = new JLabel("IContainer Format");
				optpanel.add(lblIContainerFormat);

				JTextField fld_16 = new JTextField();
				optpanel.add(fld_16);

				JLabel lblIACodec = new JLabel("IA Codec");
				optpanel.add(lblIACodec);

				JTextField fld_17 = new JTextField();
				optpanel.add(fld_17);

				JLabel lblISampleRate = new JLabel("ISample Rate");
				optpanel.add(lblISampleRate);

				JTextField fld_18 = new JTextField( );
				optpanel.add(fld_18);

				JLabel lblIChannels = new JLabel("IChannels");
				optpanel.add(lblIChannels);

				JTextField fld_19 = new JTextField( );
				optpanel.add(fld_19);

				JButton btnSubmitSpecs = new JButton("Save Specs");
				optpanel.add(btnSubmitSpecs);
				btnSubmitSpecs.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						FileSpecOptions fileSpec = new FileSpecOptions();
						fileSpec.setmHasAudio(fld.getState()) ;
						fileSpec.setmHasVideo(fld_1.getState()) ;
						fileSpec.setmRealTimeEncoder(fld_2.getState()) ;
						fileSpec.setAcodec(fld_3.getText()) ;
						fileSpec.setVcodec(fld_4.getText()) ;
						fileSpec.setContainerFormat(fld_5.getText()) ;
						fileSpec.setAstream(Integer.valueOf(fld_6.getText())) ;
						fileSpec.setAquality(Integer.valueOf(fld_7.getText())) ;
						fileSpec.setSampleRate(Integer.valueOf(fld_8.getText())) ;
						fileSpec.setChannels(Integer.valueOf(fld_9.getText())) ;
						fileSpec.setAbitrate(Integer.valueOf(fld_10.getText())) ;
						fileSpec.setVbitrate(Integer.valueOf(fld_11.getText())) ;
						fileSpec.setVbitratetolerance(Integer.valueOf(fld_12.getText())) ;
						fileSpec.setVquality(Integer.valueOf(fld_13.getText())) ;
						fileSpec.setVstream(Integer.valueOf(fld_14.getText())) ;
						fileSpec.setVscaleFactor(Double.valueOf(fld_15.getText())) ;
						fileSpec.setIcontainerFormat(fld_16.getText()) ;
						fileSpec.setIacodec(fld_17.getText()) ;
						fileSpec.setIsampleRate(Integer.valueOf(fld_18.getText())) ;
						fileSpec.setIchannels(Integer.valueOf(fld_19.getText())) ;
						//baxter.setFileSpec(fileSpec);
						optionFrame.dispose();
					}
				});

				JButton btnCancel = new JButton("Cancel");
				optpanel.add(btnCancel);
				btnCancel.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						optionFrame.dispose();
					}
				});

				optionFrame.setVisible(true);
			}
		});


	}

	public void updateFileList(ArrayList<File> files) {

		DefaultListModel<File> model = new DefaultListModel<>();
		for (File file : files) {
			model.addElement(file);
			System.out.println(file.getName());
		}
		filePanel.removeAll();
		fileList = new JList<File>(model);
		scrollPane = new JScrollPane(fileList);
		filePanel.add(scrollPane);
		fileList.revalidate();
		fileList.repaint();
		scrollPane.revalidate();
		scrollPane.repaint();
		frame.revalidate();
		frame.repaint();
	}

}
