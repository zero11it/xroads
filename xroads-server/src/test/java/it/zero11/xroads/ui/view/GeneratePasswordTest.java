package it.zero11.xroads.ui.view;

import java.util.Scanner;

import it.zero11.xroads.utils.SHA256;

public class GeneratePasswordTest {

	public static void main(String[] args) {
		String password;
		Scanner input = new Scanner(System.in);
		do {
			System.out.println("Insert password for generation of hash (insert exit to esc) : ");
			password = input.nextLine();
			System.out.println(SHA256.digest(password));
		} while(!password.equalsIgnoreCase("exit"));
		input.close();
	}

}
