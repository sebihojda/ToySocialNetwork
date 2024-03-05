package com.example.toysocialnetwork.service;

import com.example.toysocialnetwork.domain.Friendship;
import com.example.toysocialnetwork.domain.Graph;
import com.example.toysocialnetwork.domain.Tuple;
import com.example.toysocialnetwork.domain.User;
import com.example.toysocialnetwork.observer.Observable;
import com.example.toysocialnetwork.observer.Observer;
import com.example.toysocialnetwork.repository.FriendshipDBRepository;
import com.example.toysocialnetwork.repository.UserDBRepository;
import com.example.toysocialnetwork.repository.paging.Page;
import com.example.toysocialnetwork.repository.paging.Pageable;
import com.example.toysocialnetwork.utils.AdminEvent;
import com.example.toysocialnetwork.utils.ServiceType;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

public class FriendshipService implements Observable<AdminEvent> {

    private List<Observer<AdminEvent>> observers = new ArrayList<>();

    private final UserDBRepository userDBRepository;

    private final FriendshipDBRepository friendshipDBRepository;

    public FriendshipService(UserDBRepository userDBRepository, FriendshipDBRepository friendshipDBRepository){
        this.userDBRepository = userDBRepository;
        this.friendshipDBRepository = friendshipDBRepository;
    }

    public void add(Long firstUserId, Long secondUserId, LocalDateTime date){
        Friendship friendship = new Friendship(firstUserId, secondUserId, date);
        friendship.setId(new Tuple<>(firstUserId, secondUserId));
        if(friendshipDBRepository.findOne(new Tuple<>(firstUserId, secondUserId)).isPresent()
        || friendshipDBRepository.findOne(new Tuple<>(secondUserId, firstUserId)).isPresent())
            throw new IllegalArgumentException("(Service) This friendship already exists!");
        if(friendshipDBRepository.save(friendship).isPresent()){
            throw new IllegalArgumentException("(Service) SQL Statement Failed!");
        }
        notifyObservers(new AdminEvent(ServiceType.Friendship));
    }

    public void remove(Tuple<Long,Long> id){
        if(friendshipDBRepository.delete(id).isEmpty()){
            throw new IllegalArgumentException("(Service) SQL Statement Failed!");
        }
        notifyObservers(new AdminEvent(ServiceType.Friendship));
    }

    public void update(Long id1, Long id2, LocalDateTime date, String state){
        Friendship newFriendship = new Friendship(id1, id2, date);
        newFriendship.setId(new Tuple<>(id1, id2));
        newFriendship.setState(state);
        if(friendshipDBRepository.update(newFriendship).isPresent())
            throw new IllegalArgumentException("(Service) SQL Statement Failed!");
        notifyObservers(new AdminEvent(ServiceType.Friendship));
    }

    public int communities_number(){
        Optional<List<Friendship>> friendships = Optional.of(new ArrayList<Friendship>());
        friendshipDBRepository.findAll().forEach(friendship -> {friendships.get().add(friendship);});
        Optional<List<User>> users = Optional.of(new ArrayList<User>());
        userDBRepository.findAll().forEach(user->{users.get().add(user);});
        Graph g = new Graph(users.get().size());
        friendships.get().forEach(friendship ->{
            Long firstUserId = friendship.getId().getLeft();
            Long secondUserId = friendship.getId().getRight();
            int firstUserIndex = users.get().indexOf(userDBRepository.findOne(firstUserId).get());
            int secondUserIndex = users.get().indexOf(userDBRepository.findOne(secondUserId).get());
            g.addEdge(firstUserIndex, secondUserIndex);
        });
        g.DFS();
        return g.ConnectedComponentsNumber();
        //return 0;
    }

    public List<User> most_sociable_community(){
        Optional<List<Friendship>> friendships = Optional.of(new ArrayList<Friendship>());
        friendshipDBRepository.findAll().forEach(friendship -> {friendships.get().add(friendship);});
        Optional<List<User>> users = Optional.of(new ArrayList<User>());
        userDBRepository.findAll().forEach(user->{users.get().add(user);});
        Graph g = new Graph(users.get().size());
        friendships.get().forEach(friend ->{
            Long firstUserId = friend.getId().getLeft();
            Long secondUserId = friend.getId().getRight();
            int firstUserIndex = users.get().indexOf(userDBRepository.findOne(firstUserId).get());
            int secondUserIndex = users.get().indexOf(userDBRepository.findOne(secondUserId).get());
            g.addEdge(firstUserIndex,secondUserIndex);
        });
        g.DFS();
        List<User> mostSociableComponent = new ArrayList<>();
        ArrayList<ArrayList<Integer>> connectedComponents = g.ConnectedComponents();
        int maximumNumber = 0;
        for(var component: connectedComponents)
            if(maximumNumber < component.size())
                maximumNumber = component.size();
        for(var component: connectedComponents)
            if(maximumNumber == component.size()) {
                component.forEach(index -> {
                    mostSociableComponent.add(users.get().get(index));
                });
                break;
            }
        return mostSociableComponent;
        //return new ArrayList<>();
    }

    public List<String> filter_friendship(Long id, Month month){
        List<Friendship> friendships = new ArrayList<>();
        friendshipDBRepository.findAll().forEach(friendships::add);
        Predicate<Friendship> idMatching = friendship -> Objects.equals(friendship.getId().getLeft(), id)
                || Objects.equals(friendship.getId().getRight(), id);
        Predicate<Friendship> dateMatching = friendship -> friendship.getFriendsFrom().getMonth().equals(month);
        var users_friendships = friendships.stream().filter(idMatching.and(dateMatching)).toList();
        List<String> printing = new ArrayList<>();
        users_friendships.forEach(friendship -> {
            User user;
            if(!Objects.equals(friendship.getId().getLeft(), id)){
                user = userDBRepository.findOne(friendship.getId().getLeft()).get();
            }else {
                user = userDBRepository.findOne(friendship.getId().getRight()).get();
            }
            printing.add(user.getFirstName() + "|" + user.getLastName() + "|" + friendship.getFriendsFrom());
        });
        return printing;
    }

    public Iterable<Friendship> getAll(){ return friendshipDBRepository.findAll(); }

    public Page<Friendship> friendshipsOnPage(Pageable pageable){
        return friendshipDBRepository.findAll(pageable);
    }

    @Override
    public void addObserver(Observer<AdminEvent> e) {
        observers.add(e);
    }

    @Override
    public void removeObserver(Observer<AdminEvent> e) {
        observers.remove(e);
    }

    @Override
    public void notifyObservers(AdminEvent t) {
        observers.forEach(observer -> observer.update(t));
    }
}
