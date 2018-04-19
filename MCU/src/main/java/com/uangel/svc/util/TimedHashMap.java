package com.uangel.svc.util;

//package com.admtel.telephonyserver.utils;

import java.io.Serializable;
import java.util.*;

public class TimedHashMap<K,V> implements Map<K,V> {

    class TimedEntry<T,E> implements Entry<T,E>, Serializable {
        private static final long serialVersionUID = 5681390075518522107L;
        T key;
        E value;


        transient TimerTask timeoutTask = new TimerTask() {
            public void run() {
                synchronized ( TimedHashMap.this ) {
                    if ( containsKey(key) &&
                            baseMap.get( key )==TimedEntry.this) {
                        remove( key );
                    }
                }
            }
        };

        public TimedEntry(T key, E value) {
            super();
            this.key = key;
            this.value = value;
            tableTimer.schedule(timeoutTask, timeout);
        }

        public T getKey() {
            return key;
        }

        @Override
        public E setValue(E value) {
            E oldValue = this.value;
            this.value = value;
            return oldValue;
        }

        public E getValue() {
            return value;
        }
    }

    private int timeout;

    static
    private Timer tableTimer = new Timer(true);
    private Map<K, TimedEntry<K,V>> baseMap = new  LinkedHashMap<K, TimedEntry<K,V>>();


    /** crée une table de timedEntry qui se supprime si elle n'ont pas ét? modifiée au bout de timout secondes
     * @param timeout temps au bout duquels les entrées non modifiées sont supprimées
     */
    public TimedHashMap(int timeout) {
        super();
        this.timeout = timeout;
    }

    public TimedHashMap(){
        super();
        this.timeout = 300000; // 5 minutes
    }
    public void setTimeout(int timeout){
        this.timeout = timeout;
    }
    @Override
    public void clear() {
        baseMap.clear();
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new HashSet<Entry<K,V>>(baseMap.values());
    }

    @Override
    public boolean containsKey(Object key) {
        return baseMap.containsKey(key);
    }

    /**attention, ne doit pas être utilisée car n'a pas de sens !	 */
    @Override
    public boolean containsValue(Object value) {
        throw new RuntimeException( "!!! NOT IMPLEMENTED !!!");
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get( Object key) {
        TimedEntry<K,V> e=baseMap.get((K) key);
        if (e==null)
            return null;
        return e.getValue();
    }
//    public V get( Object key) {
//   		if (  ! baseMap.containsKey( (K) key ) ) {
//   			return null;
//   		}
//   		return baseMap.get((K) key).getValue();
//   	}

    @Override
    public boolean isEmpty() {
        return baseMap.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return baseMap.keySet();
    }

    /** pour ajouter une entrée avec un timer, possibilit? de commenter les ajouts	 */
    @Override
    public V put(K key, V value) {
        V oldVal = null;
        //Log.log("[TimeHashMap] adding "+key, Log.VV);
        if ( containsKey(key) ) {
            oldVal = get( key );
            //Log.log("[TimeHashMap] "+key+" already exists : overwriting", Log.VV);
        }
        baseMap.put(key, new TimedEntry<K,V>( key, value) );
        return oldVal;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for ( Entry<? extends K, ? extends V> e : m.entrySet() ) {
            put( e.getKey(), e.getValue() );
        }
    }

    @Override
    public synchronized V remove(Object key) {
        TimedEntry<K,V> entry=baseMap.remove(key);
        if (entry != null)
            return entry.getValue();

        return null;
    }

    @Override
    public int size() {
        return baseMap.size();
    }

    @Override
    public Collection<V> values() {
        Set<V> valuesSet = new HashSet<V>();
        for ( Entry<K,V> e : entrySet() ) {
            valuesSet.add(e.getValue());
        }
        return valuesSet;
    }

    /**
     * Returns a string representation of this map.  The string representation
     * consists of a list of key-value mappings in the order returned by the
     * map's <tt>entrySet</tt> view's iterator, enclosed in braces
     * (<tt>"{}"</tt>).  Adjacent mappings are separated by the characters
     * <tt>", "</tt> (comma and space).  Each key-value mapping is rendered as
     * the key followed by an equals sign (<tt>"="</tt>) followed by the
     * associated value.  Keys and values are converted to strings as by
     * {@link String#valueOf(Object)}.
     *
     * @return a string representation of this map
     */
    public String toString() {
//        Iterator<Entry<K,V>> i = entrySet().iterator();
//        if (! i.hasNext())
//            return "{}";
//
//        StringBuilder sb = new StringBuilder();
//        sb.append('{');
//        for (;;) {
//            Entry<K,V> e = i.next();
//            K key = e.getKey();
//            V value = e.getValue();
//            sb.append(key   == this ? "(this Map)" : key);
//            sb.append('=');
//            sb.append(value == this ? "(this Map)" : value);
//            if (! i.hasNext())
//                return sb.append('}').toString();
//            sb.append(',').append(' ');
//        }
        return baseMap.toString();
    }

//    public static void main(String[] args) throws Exception {
//        TimedHashMap<String,String> m=new TimedHashMap<String,String>(2000);
//        m.put("112", "33");
//
//        System.out.println(                m.get("112")        );
//        System.out.println(m);
//
//        Thread.sleep(2500);
//
//        System.out.println(                m.get("112")        );
//        System.out.println(m);
//    }
}
