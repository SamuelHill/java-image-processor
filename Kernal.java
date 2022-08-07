class Kernal {
	double[][] k;

	public Kernal() {
		this.k = new double[3][3];
	}

	public Kernal(int n) {
		if (n%2 == 1) {
			System.out.println("we don't do even kernals round these parts pinhead");
		} else {
			this.k = new double[n][n];
		}
	}

	public String toString() {
		String output = "Kernal:\n";
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				output += "\t" + this.getW(i, j);
			}
			output += "\n";
		}
		return output;
	}

	public double getW(int i, int j) {
		if (i == -1) {
			if (j == -1) {
				return this.k[0][0];
			} else if (j == 0) {
				return this.k[0][1];
			} else {
				return this.k[0][2];
			}
		} else if (i == 0) {
			if (j == -1) {
				return this.k[1][0];
			} else if (j == 0) {
				return this.k[1][1];
			} else {
				return this.k[1][2];
			}
		} else {
			if (j == -1) {
				return this.k[2][0];
			} else if (j == 0) {
				return this.k[2][1];
			} else {
				return this.k[2][2];
			}
		}
	}

	public void setW(int i, int j, double val) {
		if (i == -1) {
			if (j == -1) {
				this.k[0][0] = val;
			} else if (j == 0) {
				this.k[0][1] = val;
			} else {
				this.k[0][2] = val;
			}
		} else if (i == 0) {
			if (j == -1) {
				this.k[1][0] = val;
			} else if (j == 0) {
				this.k[1][1] = val;
			} else {
				this.k[1][2] = val;
			}
		} else {
			if (j == -1) {
				this.k[2][0] = val;
			} else if (j == 0) {
				this.k[2][1] = val;
			} else {
				this.k[2][2] = val;
			}
		}
	}

	public void makeBlur() {
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				this.setW(i, j, (1.0/9.0));
			}
		}
	}

	public void makeEdgeDetect() {
		for(int i = -1; i <= 1; i++) {
			for(int j = -1; j <= 1; j++) {
				if (i == 0 && j == 0) {
					this.setW(i, j, 8.0);
				} else {
					this.setW(i, j, -1.0);
				}
			}
		}
	}

	// FOR TESTING:
	// public static void main(String[] args) {
	// 	Kernal k = new Kernal();
	// 	k.makeBlur();
	// 	System.out.println(k.toString());
	// 	k.makeEdgeDetect();
	// 	System.out.println(k.toString());
	// }
}