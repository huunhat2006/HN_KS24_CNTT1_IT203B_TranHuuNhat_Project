package com.prj.meeting.presentation;

import java.util.Scanner;

public class ConsoleUI {
    private static Scanner scanner = new Scanner(System.in);
    
    public static void showHeader(String title) {
        System.out.println("\n=== " + title + " ===");
    }
    
    public static void showMessage(String message) {
        System.out.println(message);
    }
    
    public static String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    
    public static void showSuccess(String message) {
        System.out.println("✓ " + message);
    }
    
    public static void showError(String message) {
        System.out.println("✗ " + message);
    }
    
    public static void showMenu(String title, String[] options) {
        showHeader(title);
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
        System.out.print("Chọn: ");
    }
    
    public static int getIntInput(String prompt) {
        while (true) {
            try {
                String input = getInput(prompt);
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                showError("Vui lòng nhập một số hợp lệ!");
            }
        }
    }
    
    public static void showTable(String[] headers, String[][] data) {
        if (headers == null || data == null) return;
        
        // Calculate column widths
        int[] colWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            colWidths[i] = headers[i].length();
            for (String[] row : data) {
                if (i < row.length && row[i] != null) {
                    colWidths[i] = Math.max(colWidths[i], row[i].length());
                }
            }
        }
        
        // Print headers
        for (int i = 0; i < headers.length; i++) {
            System.out.printf("%-" + colWidths[i] + "s  ", headers[i]);
        }
        System.out.println();
        
        // Print separator
        for (int i = 0; i < headers.length; i++) {
            for (int j = 0; j < colWidths[i] + 2; j++) {
                System.out.print("-");
            }
        }
        System.out.println();
        
        // Print data rows
        for (String[] row : data) {
            for (int i = 0; i < headers.length; i++) {
                String value = (i < row.length && row[i] != null) ? row[i] : "";
                System.out.printf("%-" + colWidths[i] + "s  ", value);
            }
            System.out.println();
        }
    }

    public static boolean confirm(String message) {
        while (true) {
            String input = getInput(message + " (y/n): ").toLowerCase().trim();
            if (input.equals("y") || input.equals("yes")) {
                return true;
            } else if (input.equals("n") || input.equals("no")) {
                return false;
            } else {
                showError("Please enter 'y' for yes or 'n' for no.");
            }
        }
    }
}
