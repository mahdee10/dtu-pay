package dtu.dtuPay.repositories;

import dtu.dtuPay.models.Merchant;
import java.util.HashMap;
import java.util.UUID;

public class MerchantRepository {
    private static MerchantRepository instance;
    private HashMap<UUID, Merchant> merchants;

    private MerchantRepository() {
        merchants = new HashMap<>();
    }

    public static MerchantRepository getInstance() {
        if (instance == null) {
            synchronized (MerchantRepository.class) {
                if (instance == null) {
                    instance = new MerchantRepository();
                }
            }
        }
        return instance;
    }

    public void addMerchant(Merchant merchant) {
        merchants.put(merchant.getId(), merchant);
    }

    public Merchant getMerchant(UUID id) {
        return merchants.get(id);
    }

    public boolean removeMerchant(UUID id) {
        if (merchants.containsKey(id)) {
            System.out.println("found");
            merchants.remove(id);
            return true;
        }
        return false;
    }

    public UUID getMerchantByCPR(String cpr) {
        for (Merchant merchant : merchants.values()) {
            if (merchant.getCpr().equalsIgnoreCase(cpr)) {
                return merchant.getId();
            }
        }

        return null;
    }
}
