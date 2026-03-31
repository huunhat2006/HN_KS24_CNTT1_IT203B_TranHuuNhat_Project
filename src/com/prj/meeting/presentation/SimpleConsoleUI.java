package com.prj.meeting.presentation;

import java.util.Scanner;
import java.io.Console;

public class SimpleConsoleUI {
    private static final Scanner scanner = new Scanner(System.in);
    
    public static void clearScreen() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            for (int i = 0; i < 50; i++) System.out.println();
        }
    }
    
    public static void showHeader(String title) {
        clearScreen();
        System.out.println("+==============================================================================+");
        System.out.println("|" + centerText("MEETING ROOM MANAGEMENT SYSTEM", 78) + "|");
        System.out.println("+==============================================================================+");
        System.out.println("|" + centerText(title, 78) + "|");
        System.out.println("+==============================================================================+");
        System.out.println();
    }
    
    public static void showMenu(String title, String[] options) {
        showHeader(title);
        
        int maxWidth = 78;
        System.out.println("+==============================================================================+");
        
        for (int i = 0; i < options.length; i++) {
            String optionText = String.format("%d. %s", i + 1, options[i]);
            System.out.println("| " + padRight(optionText, maxWidth - 4) + " |");
        }
        
        System.out.println("+==============================================================================+");
        System.out.print("\nEnter your choice (1-" + options.length + "): ");
    }
    
    public static void showTable(String[] headers, String[][] data) {
        if (data == null || data.length == 0) {
            System.out.println("No data to display.");
            return;
        }
        
        int[] columnWidths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            columnWidths[i] = headers[i].length();
            for (String[] row : data) {
                if (i < row.length && row[i] != null) {
                    columnWidths[i] = Math.max(columnWidths[i], row[i].length());
                }
            }
            columnWidths[i] = Math.min(columnWidths[i], 20);
        }
        
        int totalWidth = 0;
        for (int width : columnWidths) {
            totalWidth += width + 3;
        }
        totalWidth += 1;
        
        System.out.println("+" + repeat("-", totalWidth - 2) + "+");
        
        System.out.print("|");
        for (int i = 0; i < headers.length; i++) {
            System.out.print(" " + padRight(truncate(headers[i], columnWidths[i]), columnWidths[i]) + " |");
        }
        System.out.println();
        
        System.out.println("+" + repeat("-", totalWidth - 2) + "+");
        
        for (String[] row : data) {
            System.out.print("|");
            for (int i = 0; i < headers.length; i++) {
                String cell = (i < row.length && row[i] != null) ? row[i] : "";
                System.out.print(" " + padRight(truncate(cell, columnWidths[i]), columnWidths[i]) + " |");
            }
            System.out.println();
        }
        
        System.out.println("+" + repeat("-", totalWidth - 2) + "+");
    }
    
    public static String getPasswordInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    public static String getInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
    
    public static int getIntInput(String prompt) {
        while (true) {
            try {
                String input = getInput(prompt);
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }
    
    public static void showMessage(String message) {
        System.out.println("\n" + message);
//        System.out.println("Press Enter to continue...");
//        scanner.nextLine();
    }
    
    public static void showError(String error) {
        System.out.println("\nError: " + error);
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    public static void showSuccess(String success) {
        System.out.println("\nSuccess: " + success);
        System.out.println("Press Enter to continue...");
        scanner.nextLine();
    }
    
    public static boolean confirm(String message) {
        System.out.print("\n" + message + " (Y/N): ");
        String input = scanner.nextLine().trim().toUpperCase();
        return input.equals("Y") || input.equals("YES");
    }
    
    private static String centerText(String text, int width) {
        int padding = (width - text.length()) / 2;
        return " ".repeat(Math.max(0, padding)) + text + " ".repeat(Math.max(0, width - padding - text.length()));
    }
    
    private static String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }
    
    private static String truncate(String text, int maxLength) {
        return text.length() > maxLength ? text.substring(0, maxLength - 3) + "..." : text;
    }
    
    private static String repeat(String str, int count) {
        return str.repeat(count);
    }
}
