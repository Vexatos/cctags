package boq.cctags.cc;

import static boq.utils.misc.Utils.TRUE;
import static boq.utils.misc.Utils.wrap;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import boq.cctags.CCTags;
import boq.cctags.tag.TagData;
import boq.cctags.tag.TagIcons;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;

public class PrinterHelper {
    public interface Printer {
        public void setInkLevel(int inkLevel);

        public int getInkLevel();
    }

    private final Printer owner;

    public PrinterHelper(Printer owner) {
        this.owner = owner;
    }

    private static boolean isBlackDye(ItemStack stack) {
        int oreId = OreDictionary.getOreID(stack);
        return oreId != -1 && OreDictionary.getOreName(oreId).equals("dyeBlack");
    }

    public boolean tryPrint() {
        int level = owner.getInkLevel();
        if (level <= 0)
            return false;

        owner.setInkLevel(level - 1);
        return true;
    }

    public boolean tryAdd(int amount) {
        int newAmount = owner.getInkLevel() + amount;
        if (newAmount > CCTags.config.MAX_PRINTER_CAPACITY)
            return false;

        owner.setInkLevel(newAmount);
        return true;
    }

    public boolean addInk(ItemStack stack) {
        if (isBlackDye(stack))
            return tryAdd(CCTags.config.PRINTER_USES_PER_INKSACK);

        return false;
    }

    public Object[] printTag(TagData data, String icon, String label) {
        if (!TagIcons.instance.isValidIconString(icon))
            return wrap(false, "Unknown icon: " + icon);

        if (!tryPrint())
            return wrap(false, "No ink");

        data.icon = icon;
        data.label = Strings.isNullOrEmpty(label) ? null : label;

        return TRUE;
    }

    public String printIconList(String iconType) {
        List<String> icons = TagIcons.instance.listIcons(iconType);
        return Joiner.on(',').join(icons);
    }
}
