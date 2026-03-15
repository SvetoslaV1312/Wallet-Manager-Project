package bg.sofia.uni.fmi.mjt.entity;

import java.io.Serializable;

public record ChainAddress(String chain_id, String network_id, String address) implements Serializable {

}


