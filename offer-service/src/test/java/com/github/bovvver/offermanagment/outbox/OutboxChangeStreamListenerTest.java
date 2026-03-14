package com.github.bovvver.offermanagment.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.contracts.IntegrationEvent;
import com.github.bovvver.offermanagment.events.ExecutorAssigned;
import com.mongodb.client.model.changestream.ChangeStreamDocument;
import com.mongodb.client.model.changestream.OperationType;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OutboxChangeStreamListenerTest {

    @Mock
    private EventBus eventBus;

    @Mock
    private ObjectMapper objectMapper;

    @Spy
    @InjectMocks
    private OutboxChangeStreamListener listener;

    @Test
    void shouldIgnoreNonInsertOperations() {
        ChangeStreamDocument<Document> change = mock(ChangeStreamDocument.class);
        when(change.getOperationType()).thenReturn(OperationType.UPDATE);

        callHandleChangeViaReflection(change);

        verify(eventBus, never()).publish(any());
    }

    @Test
    void shouldIgnoreWhenFullDocumentIsNull() {
        ChangeStreamDocument<Document> change = mock(ChangeStreamDocument.class);
        when(change.getOperationType()).thenReturn(OperationType.INSERT);
        when(change.getFullDocument()).thenReturn(null);

        callHandleChangeViaReflection(change);

        verify(eventBus, never()).publish(any());
    }

    @Test
    void shouldPublishEventWhenKnownTypeAndDeserializationSucceeds() throws Exception {
        Document doc = new Document();
        doc.put("type", "ExecutorAssigned");
        doc.put("payload", "{ } ");

        ChangeStreamDocument<Document> change = mock(ChangeStreamDocument.class);
        when(change.getOperationType()).thenReturn(OperationType.INSERT);
        when(change.getFullDocument()).thenReturn(doc);

        ExecutorAssigned domainEvent = mock(ExecutorAssigned.class);
        when(objectMapper.readValue("{ } ", ExecutorAssigned.class)).thenReturn(domainEvent);

        callHandleChangeViaReflection(change);

        verify(eventBus).publish(any(IntegrationEvent.class));
    }

    @Test
    void shouldNotPublishWhenUnknownEventType() {
        Document doc = new Document();
        doc.put("type", "UnknownType");
        doc.put("payload", "{ } ");

        ChangeStreamDocument<Document> change = mock(ChangeStreamDocument.class);
        when(change.getOperationType()).thenReturn(OperationType.INSERT);
        when(change.getFullDocument()).thenReturn(doc);

        callHandleChangeViaReflection(change);

        verify(eventBus, never()).publish(any());
    }

    private void callHandleChangeViaReflection(ChangeStreamDocument<Document> change) {
        try {
            var method = OutboxChangeStreamListener.class.getDeclaredMethod("handleChange", ChangeStreamDocument.class);
            method.setAccessible(true);
            method.invoke(listener, change);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
