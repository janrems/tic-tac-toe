package gui;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import logika.Igra;
import logika.Igralec;
import logika.Polje;
import logika.Poteza;
import logika.Terica;

/**
 * Glavno okno aplikacije, hrani trenutno stanje igre in nadzoruje potek
 * igre.
 * 
 * @author andrej
 *
 */
@SuppressWarnings("serial")
public class GlavnoOkno extends JFrame implements ActionListener {
	/**
	 * JPanel, v katerega rišemo X in O
	 */
	private IgralnoPolje polje;

	/**
	 * Statusna vrstica v spodnjem delu okna
	 */
	private JLabel status;

	
	/**
	 * Logika igre, null če se igra trenutno ne igra
	 */
	private Igra igra;
	
	/**
	 * Strateg, ki vleče poteze X.
	 */
	private Strateg strategX;

	/**
	 * Strateg, ki vleče poteze O
	 */
	private Strateg strategO;
	
	// Izbire v menujih
	private JMenuItem igraClovekRacunalnik;
	private JMenuItem igraRacunalnikClovek;
	private JMenuItem igraClovekClovek;
	private JMenuItem igraRacunalnikRacunalnik;

	/**
	 * Ustvari novo glavno okno in prični igrati igro.
	 */
	public GlavnoOkno() {
		this.setTitle("Tic tac toe");
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setLayout(new GridBagLayout());
		
		// menu
		JMenuBar menu_bar = new JMenuBar();
		this.setJMenuBar(menu_bar);
		JMenu igra_menu = new JMenu("Igra");
		menu_bar.add(igra_menu);

		igraClovekRacunalnik = new JMenuItem("Človek – računalnik");
		igra_menu.add(igraClovekRacunalnik);
		igraClovekRacunalnik.addActionListener(this);
		
		igraRacunalnikClovek = new JMenuItem("Računalnik – človek");
		igra_menu.add(igraRacunalnikClovek);
		igraRacunalnikClovek.addActionListener(this);

		igraRacunalnikRacunalnik = new JMenuItem("Računalnik – računalnik");
		igra_menu.add(igraRacunalnikRacunalnik);
		igraRacunalnikRacunalnik.addActionListener(this);

		igraClovekClovek = new JMenuItem("Človek – človek");
		igra_menu.add(igraClovekClovek);
		igraClovekClovek.addActionListener(this);
	
		// igralno polje
		polje = new IgralnoPolje(this);
		GridBagConstraints polje_layout = new GridBagConstraints();
		polje_layout.gridx = 0;
		polje_layout.gridy = 0;
		polje_layout.fill = GridBagConstraints.BOTH;
		polje_layout.weightx = 1.0;
		polje_layout.weighty = 1.0;
		getContentPane().add(polje, polje_layout);
		
		// statusna vrstica za sporočila
		status = new JLabel();
		status.setFont(new Font(status.getFont().getName(),
							    status.getFont().getStyle(),
							    20));
		GridBagConstraints status_layout = new GridBagConstraints();
		status_layout.gridx = 0;
		status_layout.gridy = 1;
		status_layout.anchor = GridBagConstraints.CENTER;
		getContentPane().add(status, status_layout);
		
		// začnemo novo igro človeka proti računalniku
		novaIgra(new Clovek(this, Igralec.O),
				 new Racunalnik(this, Igralec.X));
	}
	
	/**
	 * @return trenutna igralna plosča, ali null, če igra ni aktivna
	 */
	public Polje[][] getPlosca() {
		return (igra == null ? null : igra.getPlosca());
	}
	
	/**
	 * Začni igrati novo igro. Metodo lahko pokličemo kadarkoli in
	 * bo pravilno ustavila morebitno trenutno igro.
	 */
	public void novaIgra(Strateg noviSrategO, Strateg noviStrategX) {
		// Prekinemo stratege
		if (strategO != null) { strategO.prekini(); }
		if (strategX != null) { strategX.prekini(); }
		// Ustvarimo novo igro
		this.igra = new Igra();
		// Ustvarimo nove stratege
		strategO = noviSrategO;
		strategX = noviStrategX;
		// Tistemu, ki je na potezi, to povemo
		switch (igra.stanje()) {
		case NA_POTEZI_O: strategO.na_potezi(); break;
		case NA_POTEZI_X: strategX.na_potezi(); break;
		default: break;
		}
		osveziGUI();
		repaint();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == igraClovekRacunalnik) {
			novaIgra(new Clovek(this, Igralec.O),
					  new Racunalnik(this, Igralec.X));
		}
		else if (e.getSource() == igraRacunalnikClovek) {
			novaIgra(new Racunalnik(this, Igralec.O),
					  new Clovek(this, Igralec.X));
		}
		else if (e.getSource() == igraRacunalnikRacunalnik) {
			novaIgra(new Racunalnik(this, Igralec.O),
					  new Racunalnik(this, Igralec.X));
		}
		else if (e.getSource() == igraClovekClovek) {
			novaIgra(new Clovek(this, Igralec.O),
			          new Clovek(this, Igralec.X));
		}
	}

	public void odigraj(Poteza p) {
		igra.odigraj(p);
		osveziGUI();
		switch (igra.stanje()) {
		case NA_POTEZI_O: strategO.na_potezi(); break;
		case NA_POTEZI_X: strategX.na_potezi(); break;
		case ZMAGA_O: break;
		case ZMAGA_X: break;
		case NEODLOCENO: break;
		}
	}
	
	public void osveziGUI() {
		if (igra == null) {
			status.setText("Igra ni v teku.");
		}
		else {
			switch(igra.stanje()) {
			case NA_POTEZI_O: status.setText("Na potezi je O"); break;
			case NA_POTEZI_X: status.setText("Na potezi je X"); break;
			case ZMAGA_O: status.setText("Zmagal je O"); break;
			case ZMAGA_X: status.setText("Zmagal je X"); break;
			case NEODLOCENO: status.setText("Neodločeno!"); break;
			}
		}
		polje.repaint();
	}
	
	/**
	 * Metoda, ki pravilno ukrepa, ko uporabnik klikne na polje (i,j).
	 * 
	 * @param i
	 * @param j
	 */
	public void klikniPolje(int i, int j) {
		if (igra != null) {
			switch (igra.stanje()) {
			case NA_POTEZI_X:
				strategX.klik(i, j);
				break;
			case NA_POTEZI_O:
				strategO.klik(i, j);
				break;
			default:
				break;
			}
		}		
	}

	/**
	 * @return zmagovalna terica, če obstaja, sicer null.
	 */
	public Terica zmagovalnaTerica() {
		return igra.zmagovalnaTerica();
	}
	/**
	 * @return kopija trenutne igre
	 */
	public Igra copyIgra() {
		return new Igra(igra);
	}
}
