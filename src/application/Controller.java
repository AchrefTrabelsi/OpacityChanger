package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.collections.ListChangeListener.Change;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Menu;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;

public class Controller implements Initializable {
	private String selecteditem = "";
	private HashMap<String, OpacityChanger> images = new HashMap<String, OpacityChanger>();
	private HashMap<String, LinkedList<WritableImage>> imagesmulti = new HashMap<String, LinkedList<WritableImage>>();
	private AnimationTimer t;
	double time = 0, dop = 0;
	//////
	@FXML
	private Menu FileMenu;
	@FXML
	private Button savebutton;
	@FXML
	private Button saveallbutton;
	@FXML
	private Button showbutton;
	@FXML
	private RadioButton simple;
	@FXML
	private ToggleGroup tg1;
	@FXML
	private Label opacity;
	@FXML
	private Slider opacityslider;
	@FXML
	private RadioButton multiple;
	@FXML
	private TextField step;
	@FXML
	private TextField timer;
	@FXML
	private Button deletebutton;
	@FXML
	private ListView<String> imagelist;
	@FXML
	private GridPane gp;
	@FXML
	private Canvas c;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		gp.widthProperty().addListener((v, oldv, newv) -> {
			c.setWidth(newv.doubleValue());
			draw();
		});
		gp.heightProperty().addListener((v, oldv, newv) -> {
			c.setHeight(newv.doubleValue());
			draw();
		});
		opacity.textProperty().bind(Bindings.format("Opacity : %.2f", opacityslider.valueProperty()));
		imagelist.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		step.textProperty().addListener((obs, ov, nv) -> {
			if (nv.isEmpty()) {
				step.setText("0.");
				return;
			}
			if (!nv.matches("\\d.\\d*")) {
				step.setText("0.");
			} else {
				if (Double.parseDouble(nv) >= 1) {
					step.setText("0.");
				}
			}
		});
		timer.textProperty().addListener((obs, ov, nv) -> {
			if (!nv.matches("\\d*\\d.\\d*") && !nv.matches("\\d*")) {
				timer.setText(ov);
			}
		});
		simple.selectedProperty().addListener((val, ov, nv) -> {
			if (nv) {
				showbutton.setDisable(true);
				simpleinit();
				draw();
			} else {
				if (selecteditem != "") {
					showbutton.setDisable(false);
				}
				c.getGraphicsContext2D().clearRect(0, 0, c.getWidth(), c.getHeight());
				c.getGraphicsContext2D().fillRect(0, 0, c.getWidth(), c.getHeight());
			}
		});

		imagelist.getSelectionModel().getSelectedItems().addListener((Change<? extends String> c) -> {
			if (imagelist.getSelectionModel().getSelectedItems().isEmpty()) {
				showbutton.setDisable(true);
				savebutton.setDisable(true);
				selecteditem = "";
			} else {
				savebutton.setDisable(false);
				if (imagelist.getSelectionModel().getSelectedItems().size() == 1) {
					selecteditem = imagelist.getSelectionModel().getSelectedItems().get(0);
					draw();
					if (multiple.isSelected()) {
						showbutton.setDisable(false);
					}
				}

			}
		});

		imagelist.getItems().addListener((Change<? extends String> c) -> {
			if (imagelist.getItems().isEmpty()) {
				deletebutton.setDisable(true);
				saveallbutton.setDisable(true);
			} else {
				deletebutton.setDisable(false);
				saveallbutton.setDisable(false);
			}
		});
		opacityslider.valueProperty().addListener((values, ov, nv) -> {
			updateopacity(nv.doubleValue());
			draw();
		});
		c.getGraphicsContext2D().setFill(Color.WHITE);
	}

	private void draw() {
		if (simple.isSelected()) {
			c.getGraphicsContext2D().clearRect(0, 0, c.getWidth(), c.getHeight());
			c.getGraphicsContext2D().fillRect(0, 0, c.getWidth(), c.getHeight());
			if (selecteditem != "") {
				images.get(selecteditem).updateimage();
				centerdraw(images.get(selecteditem).getimage());
			}
		} else {
			if (t == null) {
				c.getGraphicsContext2D().clearRect(0, 0, c.getWidth(), c.getHeight());
				c.getGraphicsContext2D().fillRect(0, 0, c.getWidth(), c.getHeight());
			}
		}
	}

	private void updateopacity(double p) {
		for (OpacityChanger m : images.values()) {
			m.setopacity(p);
		}
	}

	@FXML
	private void choosefiles() {
		FileChooser x = new FileChooser();
		x.getExtensionFilters().add(new ExtensionFilter("PNG (*.png)", "*.png"));
		List<File> imagesp;
		imagesp = x.showOpenMultipleDialog(Main.window);
		if (imagesp == null) {
			return;
		}
		for (File m : imagesp) {
			String name = m.getName();
			imagelist.getItems().add(name);
			images.put(m.getName(), new OpacityChanger(m.toURI().toString()));
			images.get(m.getName()).setopacity(opacityslider.getValue());
		}

	}

	@FXML
	private void delete() {
		List<String> itemstodel = imagelist.getSelectionModel().getSelectedItems();
		for (String x : itemstodel) {
			images.remove(x);
		}
		imagelist.getItems().removeAll(itemstodel);
		selecteditem = imagelist.getSelectionModel().getSelectedItem();
		if (selecteditem == null) {
			selecteditem = "";
		}
		draw();
	}

	@FXML
	private void simpleinit() {
		for (OpacityChanger m : images.values()) {
			m.setopacity(opacityslider.getValue());
		}
	}

	@FXML
	private String multipleinit() {
		String name = selecteditem;
		if (images.isEmpty()) {
			return name;
		}
		OpacityChanger x = images.get(name);
		LinkedList<WritableImage> imgs = new LinkedList<WritableImage>();
		for (double op = 1; op >= 0; op -= dop) {

			imgs.add(x.getupdatedcopy(op));
		}
		imagesmulti.put(name, imgs);
		return name;
	}

	private void drawmulti(String name) {
		if (name == "") {
			return;
		}

		t = new AnimationTimer() {
			private long earlier = 0;
			private double accumulator = 0;
			int i = 0;

			@Override
			public void handle(long now) {
				if (earlier == 0) {
					earlier = now;
					return;
				}
				accumulator += (now - earlier) / 1.0e9;
				earlier = now;
				while (accumulator > time) {
					i++;
					accumulator -= time;
				}
				i = i % imagesmulti.get(name).size();
				c.getGraphicsContext2D().clearRect(0, 0, c.getWidth(), c.getHeight());
				centerdraw(imagesmulti.get(name).get(i));
			}
		};
		t.start();
	}

	@FXML
	private void showclicked() {
		dop = Double.parseDouble(step.getText());
		time = Double.parseDouble(timer.getText());
		if (time == 0 || dop == 0) {
			return;
		}
		if (simple.isSelected()) {
			return;
		}
		if (selecteditem == "") {
			return;
		}
		disableenablecontrols();
		if (showbutton.getText().compareTo("Show") == 0) {
			showbutton.setText("Stop");
			multipleinit();
			drawmulti(selecteditem);

		} else {
			showbutton.setText("Show");
			t.stop();
			t = null;
			c.getGraphicsContext2D().clearRect(0, 0, c.getWidth(), c.getHeight());
			c.getGraphicsContext2D().fillRect(0, 0, c.getWidth(), c.getHeight());
		}
	}

	private void disableenablecontrols() {
		deletebutton.setDisable(!deletebutton.isDisabled());
		imagelist.setDisable(!imagelist.isDisable());
		simple.setDisable(!simple.isDisabled());
		imagelist.setDisable(!imagelist.isDisabled());
		savebutton.setDisable(!savebutton.isDisabled());
		saveallbutton.setDisable(!saveallbutton.isDisabled());
	}

	@FXML
	private void saveallclicked() {
		opacityslider.setDisable(true);
		step.setDisable(true);
		imagelist.setDisable(true);
		simple.setDisable(true);
		multiple.setDisable(true);
		dop = Double.parseDouble(step.getText());
		DirectoryChooser x = new DirectoryChooser();
		File savingdir = x.showDialog(Main.window);
		if (savingdir != null) {
			String temp = selecteditem;
			if (simple.isSelected()) {
				simpleinit();
				for (int i = 0; i < imagelist.getItems().size(); i++) {
					selecteditem = imagelist.getItems().get(i);
					String name = selecteditem.substring(0, selecteditem.length() - 4);
					images.get(selecteditem).updateimage();
					saveimg(savingdir, images.get(selecteditem).getimage(),
							name + String.format("_%.2f", opacityslider.getValue()));
				}
			} else {
				for (int i = 0; i < imagelist.getItems().size(); i++) {
					selecteditem = imagelist.getItems().get(i);
					String name = selecteditem.substring(0, selecteditem.length() - 4);
					multipleinit();
					int k = 0;
					for (WritableImage img : imagesmulti.get(selecteditem)) {
						saveimg(savingdir, img, name + String.format("_%.2f", (1 - dop * k)));
						k++;
					}
				}
			}
			selecteditem = temp;
		}
		opacityslider.setDisable(false);
		step.setDisable(false);
		imagelist.setDisable(false);
		simple.setDisable(false);
		multiple.setDisable(false);

	}

	@FXML
	private void saveselectedclicked() {
		opacityslider.setDisable(true);
		step.setDisable(true);
		imagelist.setDisable(true);
		simple.setDisable(true);
		multiple.setDisable(true);
		dop = Double.parseDouble(step.getText());
		String name = selecteditem.substring(0, selecteditem.length() - 4);
		DirectoryChooser x = new DirectoryChooser();
		File savingdir = x.showDialog(Main.window);
		if (savingdir != null) {
			if (simple.isSelected()) {
				simpleinit();
				images.get(selecteditem).updateimage();
				saveimg(savingdir, images.get(selecteditem).getimage(),
						name + String.format("_%.2f", opacityslider.getValue()));
			} else {
				multipleinit();
				int i = 0;
				for (WritableImage img : imagesmulti.get(selecteditem)) {
					saveimg(savingdir, img, name + String.format("_%.2f", (1 - dop * i)));
					i++;
				}
			}
		}
		opacityslider.setDisable(false);
		step.setDisable(false);
		imagelist.setDisable(false);
		simple.setDisable(false);
		multiple.setDisable(false);
	}

	private void saveimg(File dir, WritableImage img, String name) {
		File output = new File(dir + "\\" + name + ".png");
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", output);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void centerdraw(WritableImage img) {
		double x = c.getWidth() / img.getWidth();
		double y = c.getHeight() / img.getHeight();
		double w, h;
		if (x > y) {
			w = y * img.getWidth();
			h = y * img.getHeight();
			x = (c.getWidth() - w) / 2;
			if (x < 0) {
				x = 0;
			}
			c.getGraphicsContext2D().drawImage(img, x, 0, w, h);
		} else {
			w = x * img.getWidth();
			h = x * img.getHeight();
			y = (c.getHeight() - h) / 2;
			if (y < 0) {
				y = 0;
			}
			c.getGraphicsContext2D().drawImage(img, 0, y, w, h);

		}
	}
}
