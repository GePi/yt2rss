package farm.giggle.yt2rss.services;

import farm.giggle.yt2rss.model.Invite;
import farm.giggle.yt2rss.model.repo.InviteRepo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InviteService {

    private final InviteRepo inviteRepo;

    public InviteService(InviteRepo inviteRepo) {
        this.inviteRepo = inviteRepo;
    }

    @Transactional
    public boolean captureCode(String inviteCode) {
        if (inviteRepo.selectInvite(inviteCode) != null) {
            inviteRepo.deleteInviteByCode(inviteCode);
            return true;
        }
        return false;
    }

    public void addInvite(String invCode) {
        inviteRepo.save(new Invite(invCode));
    }
}
