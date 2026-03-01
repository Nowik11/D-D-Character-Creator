package backend.data;

import java.util.List;

public record ItemChoice(List<Choice> optionA, List<Choice> optionB) {
   public record Choice(String name, int count){}
}
