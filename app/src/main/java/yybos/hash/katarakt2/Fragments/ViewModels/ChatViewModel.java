package yybos.hash.katarakt2.Fragments.ViewModels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import yybos.hash.katarakt2.Socket.Objects.Message;

public class ChatViewModel extends ViewModel {
    private final MutableLiveData<List<Message>> chatHistory = new MutableLiveData<>();

    // This method allows fragments to observe changes in chat history.
    public LiveData<List<Message>> getChatHistory() {
        return chatHistory;
    }

    // This method adds a new message to the chat history.
    public void addMessage(Message message) {
        List<Message> currentHistory = chatHistory.getValue();
        if (currentHistory == null) {
            currentHistory = new ArrayList<>();
        }

        currentHistory.add(message);
        chatHistory.postValue(currentHistory);
    }
}

