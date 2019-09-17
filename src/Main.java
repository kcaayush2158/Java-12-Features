public class Main {
    public static void main(String[] args) {
        
        // you can write a multiple single line of switch case using a lamba expressionion
        String name = "sunday";
        switch (name) {
            case "monday", "tuesday", "wednesday", "thursday", "friday" -> System.out.println("weekend");
            case "saturday", "sunday" -> System.out.println("weekend");
            default -> System.out.println("invalid");
        }

        //you can write a string literal multiple line with the single quote
        String html = ` <html>
                            <body>
                                   <p>hello world</p>
                            </body>
                         </html>';

    }
}
