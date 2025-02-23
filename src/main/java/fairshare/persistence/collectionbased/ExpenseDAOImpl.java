package fairshare.persistence.collectionbased;



import fairshare.model.Expense;
import fairshare.model.PaymentRequest;
import fairshare.model.Person;
import fairshare.persistence.ExpenseDAO;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ExpenseDAOImpl implements ExpenseDAO {
    private final Map<UUID, Expense> expenses;

    public ExpenseDAOImpl() {
        expenses = new ConcurrentHashMap<>();
    }

    public ExpenseDAOImpl(Map<UUID, Expense> expenses) {
        this.expenses = expenses;
    }

    @Override
    public Collection<Expense> findExpensesForPerson(Person person) {
        return expenses.values().stream()
                .filter(e -> e.getPerson().equals(person))
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Expense save(Expense expense) {
        expenses.put(expense.getId(), expense);
        return expense;
    }

    @Override
    public Optional<Expense> get(UUID id) {
        return expenses.containsKey(id) ? Optional.of(expenses.get(id)) : Optional.empty();
    }

    @Override
    public Collection<PaymentRequest> findPaymentRequestsSent(Person person) {
        return findExpensesForPerson(person).stream()
                .map(Expense::listOfPaymentRequests)
                .flatMap(Collection::stream)
                .collect(Collectors.toUnmodifiableList());
    }

    @Override
    public Collection<PaymentRequest> findPaymentRequestsReceived(Person person) {
        return expenses.values().stream()
                .map(Expense::listOfPaymentRequests)
                .flatMap(Collection::stream)
                .filter(pr -> pr.getPersonWhoShouldPayBack().equals(person))
                .collect(Collectors.toUnmodifiableList());
    }
}
